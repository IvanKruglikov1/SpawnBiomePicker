package com.batonmc.setspawnbiome;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.*;

public class BiomeSelectScreen extends Screen {
    private final Screen parent;
    private final List<ResourceLocation> allBiomes;
    private final Set<ResourceLocation> chosen = new LinkedHashSet<>(ModConfig.selectedSpawnBiomes);
    private BiomeList list;
    private EditBox search;
    private String filter = "";

    public BiomeSelectScreen(Screen parent) {
        super(Component.translatable("setspawnbiome.screen.title"));
        this.parent = parent;
        this.allBiomes = collectBiomes(parent);
    }

    public static Component displayName(ResourceLocation id) {
        String key = id.toLanguageKey("biome");
        return I18n.exists(key) ? Component.translatable(key) : Component.literal(id.toString());
    }

    private static List<ResourceLocation> collectBiomes(Screen parent) {
        Set<ResourceLocation> result = new HashSet<>();
        try {
            if (parent instanceof CreateWorldScreen cws) {
                WorldCreationContext ctx = cws.getUiState().getSettings();
                LevelStem stem = ctx.selectedDimensions().dimensions().get(LevelStem.OVERWORLD);
                if (stem != null) {
                    for (Holder<Biome> h : stem.generator().getBiomeSource().possibleBiomes()) {
                        h.unwrapKey().ifPresent(k -> result.add(k.location()));
                    }
                }
                if (result.isEmpty()) {
                    Registry<Biome> reg = ctx.worldgenRegistries().compositeAccess().registryOrThrow(Registries.BIOME);
                    result.addAll(reg.keySet());
                }
            }
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn("Не удалось получить биомы мира, беру весь реестр", e);
        }
        if (result.isEmpty()) {
            VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME)
                    .listElementIds().forEach(k -> result.add(k.location()));
        }
        List<ResourceLocation> sorted = new ArrayList<>(result);
        sorted.sort(Comparator.comparing(id -> displayName(id).getString()));
        return sorted;
    }

    @Override
    protected void init() {
        search = new EditBox(this.font, this.width / 2 - 140, 22, 280, 18,
                Component.translatable("setspawnbiome.screen.search"));
        search.setHint(Component.translatable("setspawnbiome.screen.search"));
        search.setValue(filter);
        search.setResponder(text -> {
            filter = text;
            list.refresh(text);
        });
        addRenderableWidget(search);

        list = new BiomeList(this.minecraft, this.width, this.height - 92, 46);
        addRenderableWidget(list);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> confirm())
                .bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> onClose())
                .bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());

        list.refresh(filter);
        setInitialFocus(search);
    }

    private void confirm() {
        ModConfig.selectedSpawnBiomes = List.copyOf(chosen);
        this.minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        g.drawCenteredString(this.font, Component.translatable("setspawnbiome.screen.experimental"),
                this.width / 2, this.height - 42, 0xFFFF55);
    }

    private class BiomeList extends ObjectSelectionList<BiomeList.Entry> {
        BiomeList(Minecraft mc, int width, int height, int y) {
            super(mc, width, height, y, 20);
        }

        @Override
        public int getRowWidth() {
            return 280;
        }

        void refresh(String text) {
            clearEntries();
            String f = text.toLowerCase(Locale.ROOT).trim();
            Entry firstChosen = null;

            if (f.isEmpty()) addEntry(new Entry(null));
            for (ResourceLocation id : allBiomes) {
                if (!f.isEmpty()
                        && !displayName(id).getString().toLowerCase(Locale.ROOT).contains(f)
                        && !id.toString().contains(f)) continue;
                Entry e = new Entry(id);
                addEntry(e);
                if (firstChosen == null && chosen.contains(id)) firstChosen = e;
            }
            if (firstChosen != null) centerScrollOn(firstChosen);
            else setScrollAmount(0);
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            final ResourceLocation id; // null = по умолчанию
            final Component name;

            Entry(ResourceLocation id) {
                this.id = id;
                this.name = id == null
                        ? Component.translatable("setspawnbiome.screen.default")
                        : displayName(id);
            }

            @Override
            public void render(GuiGraphics g, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovering, float partialTick) {
                var font = Minecraft.getInstance().font;
                boolean on = id == null ? chosen.isEmpty() : chosen.contains(id);
                int color = on ? 0x55FF55 : 0xFFFFFF;
                g.drawString(font, Component.literal(on ? "[x] " : "[ ] ").append(name), left + 4, top + 6, color, false);
                if (id != null) {
                    String s = id.toString();
                    int w = font.width(s);
                    if (font.width(name) + w + 40 < width) {
                        g.drawString(font, s, left + width - w - 4, top + 6, 0x808080, false);
                    }
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (id == null) {
                    chosen.clear();
                } else if (!chosen.remove(id)) {
                    chosen.add(id);
                }
                return true;
            }

            @Override
            public Component getNarration() {
                return name;
            }
        }
    }
}