package com.batonmc.setspawnbiome;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = "setspawnbiome", value = Dist.CLIENT)
public class ClientSpawnReset {
    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        // Возврат к списку миров или в меню = создание отменено
        if (event.getNewScreen() instanceof SelectWorldScreen || event.getNewScreen() instanceof TitleScreen) {
            ModConfig.selectedSpawnBiomes = java.util.List.of();
        }
    }
}