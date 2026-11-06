package com.batonmc.setspawnbiome;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WorldSpawnSetup {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int GRID_STEP = 64;
    private static final int[] SAMPLE_Y = {64, 160};
    private static final long TIME_LIMIT_NANOS = 30_000_000_000L;

    private static volatile Component pendingMessage = null;

    public static void onServerStarted(MinecraftServer server) {
        pendingMessage = null;

        List<ResourceLocation> biomes = ModConfig.selectedSpawnBiomes;
        ModConfig.selectedSpawnBiomes = List.of(); // выбор одноразовый

        if (biomes.isEmpty()) return;

        ServerLevel overworld = server.overworld();
        if (overworld.getGameTime() > 0) return; // только для только что созданного мира

        applyCustomSpawn(overworld, biomes);
    }

    public static void onPlayerJoin(ServerPlayer player) {
        Component message = pendingMessage;
        if (message != null) {
            player.sendSystemMessage(message);
            pendingMessage = null;
        }
    }

    private static void applyCustomSpawn(ServerLevel level, List<ResourceLocation> biomeIds) {
        int radius = SpawnConfig.searchRadius;

        List<ResourceKey<Biome>> keys = new ArrayList<>();
        for (ResourceLocation id : biomeIds) keys.add(ResourceKey.create(Registries.BIOME, id));

        BlockPos found = keys.size() == 1
                ? findSingle(level, keys.get(0), radius)
                : findCombination(level, keys, radius, SpawnConfig.combinationDistance);

        if (found == null) {
            LOGGER.warn("Биомы {} не найдены в радиусе {} блоков, спавн оставлен по умолчанию", biomeIds, radius);
            MutableComponent names = Component.empty();
            for (int i = 0; i < biomeIds.size(); i++) {
                if (i > 0) names.append(", ");
                names.append(Component.translatable(biomeIds.get(i).toLanguageKey("biome")));
            }
            pendingMessage = Component.translatable(
                    keys.size() == 1 ? "setspawnbiome.message.not_found" : "setspawnbiome.message.not_found_multi",
                    names, radius
            ).withStyle(ChatFormatting.YELLOW);
            return;
        }

        int x = found.getX();
        int z = found.getZ();
        ChunkAccess chunk = level.getChunk(x >> 4, z >> 4);
        int y = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 1;

        level.setDefaultSpawnPos(new BlockPos(x, y, z), 0.0f);
        LOGGER.info("Спавн установлен в биоме(ах) {} на {}, {}, {}", biomeIds, x, y, z);
    }

    private static BlockPos findSingle(ServerLevel level, ResourceKey<Biome> key, int radius) {
        Pair<BlockPos, Holder<Biome>> found = level.findClosestBiome3d(
                holder -> holder.is(key), level.getSharedSpawnPos(), radius, 32, 64);
        return found == null ? null : found.getFirst();
    }

    private static BlockPos findCombination(ServerLevel level, List<ResourceKey<Biome>> keys,
                                            int radius, int proximity) {
        BiomeSource source = level.getChunkSource().getGenerator().getBiomeSource();
        Climate.Sampler sampler = level.getChunkSource().randomState().sampler();
        BlockPos origin = level.getSharedSpawnPos();
        long deadline = System.nanoTime() + TIME_LIMIT_NANOS;
        int maxRing = radius / GRID_STEP;

        for (int ring = 0; ring <= maxRing; ring++) {
            if (System.nanoTime() > deadline) {
                LOGGER.warn("Поиск комбинации биомов прерван по времени");
                return null;
            }
            for (int dx = -ring; dx <= ring; dx++) {
                boolean edge = Math.abs(dx) == ring;
                int dzStep = edge ? 1 : Math.max(1, 2 * ring);
                for (int dz = -ring; dz <= ring; dz += dzStep) {
                    int cx = origin.getX() + dx * GRID_STEP;
                    int cz = origin.getZ() + dz * GRID_STEP;

                    if (!hasBiomeNear(source, sampler, keys.get(0), cx, cz, 0)) continue;

                    boolean all = true;
                    for (int i = 1; i < keys.size() && all; i++) {
                        all = hasBiomeNear(source, sampler, keys.get(i), cx, cz, proximity);
                    }
                    if (all) return new BlockPos(cx, 0, cz);
                }
            }
        }
        return null;
    }

    private static boolean hasBiomeNear(BiomeSource source, Climate.Sampler sampler,
                                        ResourceKey<Biome> key, int cx, int cz, int range) {
        for (int ox = -range; ox <= range; ox += 16) {
            for (int oz = -range; oz <= range; oz += 16) {
                for (int y : SAMPLE_Y) {
                    Holder<Biome> b = source.getNoiseBiome(
                            QuartPos.fromBlock(cx + ox), QuartPos.fromBlock(y), QuartPos.fromBlock(cz + oz), sampler);
                    if (b.is(key)) return true;
                }
            }
        }
        return false;
    }
}