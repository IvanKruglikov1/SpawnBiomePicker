package com.batonmc.setspawnbiome.mixin;

import com.batonmc.setspawnbiome.BiomeSelectScreen;
import com.batonmc.setspawnbiome.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class WorldTabMixin extends GridLayoutTab {

    // Конструктор нужен только для компиляции, Mixin его отбрасывает
    protected WorldTabMixin(Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setspawnbiome$addBiomeButton(CallbackInfo ci) {
        List<ResourceLocation> sel = ModConfig.selectedSpawnBiomes;
        Component text = sel.isEmpty()
                ? Component.translatable("setspawnbiome.button.spawn_biome")
                : sel.size() == 1
                    ? Component.translatable("setspawnbiome.button.spawn_biome_value", BiomeSelectScreen.displayName(sel.get(0)))
                    : Component.translatable("setspawnbiome.button.spawn_biome_multi", sel.size());

        Button button = Button.builder(text, btn -> {
                    Minecraft mc = Minecraft.getInstance();
                    mc.setScreen(new BiomeSelectScreen(mc.screen));
                })
                .width(150)
                .build();

        int[] rows = {0};
        this.layout.visitChildren(e -> rows[0]++);
        this.layout.addChild(button, rows[0], 0);
    }
}