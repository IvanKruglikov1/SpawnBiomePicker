package com.batonmc.setspawnbiome;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod("setspawnbiome")
public class SetSpawnBiome {

    public SetSpawnBiome(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SpawnConfig.SPEC);
    }
}