package com.batonmc.setspawnbiome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class SpawnConfig {
    public static int searchRadius = 10000;      // 100..30000
    public static int combinationDistance = 128; // 32..512

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("setspawnbiome.json");

    private static class Data {
        int searchRadius = 10000;
        int combinationDistance = 128;
    }

    public static void load() {
        Data d = new Data();
        try {
            if (Files.exists(FILE)) {
                Data read = GSON.fromJson(Files.readString(FILE), Data.class);
                if (read != null) d = read;
            }
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn("Не удалось прочитать конфиг, беру значения по умолчанию", e);
        }
        searchRadius = Math.max(100, Math.min(30000, d.searchRadius));
        combinationDistance = Math.max(32, Math.min(512, d.combinationDistance));
        d.searchRadius = searchRadius;
        d.combinationDistance = combinationDistance;
        try {
            Files.writeString(FILE, GSON.toJson(d));
        } catch (Exception ignored) {
        }
    }
}