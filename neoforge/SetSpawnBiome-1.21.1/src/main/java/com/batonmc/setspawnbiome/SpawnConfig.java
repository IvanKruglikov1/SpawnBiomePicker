package com.batonmc.setspawnbiome;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SpawnConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue SEARCH_RADIUS;
    public static final ModConfigSpec.IntValue COMBINATION_DISTANCE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        SEARCH_RADIUS = builder
                .comment("How far (in blocks) to search for the selected biome.",
                         "Larger values find rare biomes more often, but world creation may take longer.")
                .translation("setspawnbiome.configuration.searchRadius")
                .defineInRange("searchRadius", 10000, 100, 30000);

        COMBINATION_DISTANCE = builder
                .comment("Experimental: when several biomes are selected, each of them must be",
                         "within this distance (in blocks) of the spawn point.")
                .translation("setspawnbiome.configuration.combinationDistance")
                .defineInRange("combinationDistance", 128, 32, 512);

        SPEC = builder.build();
    }
}