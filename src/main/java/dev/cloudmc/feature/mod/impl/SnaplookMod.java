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
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class SnaplookMod extends Mod {
    private boolean cameraToggled = false;

    private Setting keybindingSetting;

    public SnaplookMod() {
        super(
                "Snaplook",
                "Allows you to see you in 3rd person, by only holding a button.",
                Type.Mechanic
        );

        keybindingSetting = new Setting("Keybinding", this, Keyboard.KEY_F);
        Cloud.INSTANCE.settingManager.addSetting(keybindingSetting);
    }

    @Override
    public void onDisable() {
        if (cameraToggled) {
            cameraToggled = false;
            Cloud.INSTANCE.mc.gameSettings.thirdPersonView = 0;
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        if(Keyboard.isKeyDown(keybindingSetting.getKey()) && !cameraToggled){
            cameraToggled = true;
            Cloud.INSTANCE.mc.gameSettings.thirdPersonView = 1;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e){
        if(!Keyboard.isKeyDown(keybindingSetting.getKey()) && cameraToggled){
            cameraToggled = false;
            Cloud.INSTANCE.mc.gameSettings.thirdPersonView = 0;
        }
    }
}
