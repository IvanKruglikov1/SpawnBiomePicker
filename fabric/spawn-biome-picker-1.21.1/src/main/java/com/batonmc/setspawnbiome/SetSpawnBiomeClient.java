package com.batonmc.setspawnbiome;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;

import java.util.List;

public class SetSpawnBiomeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SpawnConfig.load();

        ServerLifecycleEvents.SERVER_STARTED.register(WorldSpawnSetup::onServerStarted);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                WorldSpawnSetup.onPlayerJoin(handler.getPlayer()));

        // Возврат в меню или к списку миров = создание мира отменено, сбрасываем выбор
        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> {
            if (screen instanceof SelectWorldScreen || screen instanceof TitleScreen) {
                ModConfig.selectedSpawnBiomes = List.of();
            }
        });
    }
}