package com.batonmc.setspawnbiome;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ModConfig {
    /** Выбранные биомы (пусто = по умолчанию). Первый — основной. */
    public static volatile List<ResourceLocation> selectedSpawnBiomes = List.of();
}