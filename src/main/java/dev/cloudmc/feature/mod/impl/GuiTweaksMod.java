/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiTweaksMod extends Mod {

    private static GuiTweaksMod instance;

    private Setting blurBackgroundSetting;
    private Setting darkenBackgroundSetting;

    public GuiTweaksMod() {
        super(
                "Gui Tweaks",
                "Adds Tweaks to the Gui like blur and transparency.",
                Type.Tweaks
        );
        instance = this;

        blurBackgroundSetting = new Setting("Blur Background", this, true);
        darkenBackgroundSetting = new Setting("Darken Background", this, true);

        Cloud.INSTANCE.settingManager.addSetting(blurBackgroundSetting);
        Cloud.INSTANCE.settingManager.addSetting(darkenBackgroundSetting);
    }

    /** Used by GuiScreenMixin to check darken background without string lookups. */
    public static boolean isDarkenBackgroundEnabled() {
        return instance != null && instance.isToggled() && instance.darkenBackgroundSetting.isCheckToggled();
    }

    /** Whether the darken-background setting should be used (mod must be toggled). */
    public static boolean shouldDarkenBackground() {
        // Darken background if: mod is off (vanilla behavior), OR mod is on and setting is checked
        return instance == null || !instance.isToggled() || instance.darkenBackgroundSetting.isCheckToggled();
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e) {
        if (blurBackgroundSetting.isCheckToggled()) {
            if (!(e.gui instanceof GuiChat)) {
                try {
                    Cloud.INSTANCE.mc.entityRenderer.loadShader(
                            new ResourceLocation("shaders/post/blur.json"));
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
            if (e.gui == null) {
                if (Cloud.INSTANCE.mc.entityRenderer.getShaderGroup() != null) {
                    Cloud.INSTANCE.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
            }
        }
    }
}