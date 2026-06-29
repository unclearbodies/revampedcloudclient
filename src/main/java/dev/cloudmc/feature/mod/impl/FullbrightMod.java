/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FullbrightMod extends Mod {

    private float oldGamma;
    private float lastTargetGamma;
    private Setting brightnessSetting;

    public FullbrightMod() {
        super(
                "Fullbright",
                "Changes the Gamma of the game to a given value.",
                Type.Visual
        );

        brightnessSetting = new Setting("Brightness", this, 100, 10);
        Cloud.INSTANCE.settingManager.addSetting(brightnessSetting);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        oldGamma = Cloud.INSTANCE.mc.gameSettings.gammaSetting;
        lastTargetGamma = -1; // Force update
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Cloud.INSTANCE.mc.gameSettings.gammaSetting = oldGamma;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        float targetGamma = brightnessSetting.getCurrentNumber();
        float currentGamma = Cloud.INSTANCE.mc.gameSettings.gammaSetting;
        if (currentGamma != lastTargetGamma && lastTargetGamma != -1) {
            // User changed gamma in vanilla settings while mod was enabled
            oldGamma = currentGamma;
        }
        if (currentGamma != targetGamma) {
            Cloud.INSTANCE.mc.gameSettings.gammaSetting = targetGamma;
            lastTargetGamma = targetGamma;
        }
    }
}
