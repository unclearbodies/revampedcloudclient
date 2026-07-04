/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ToggleSneakMod extends Mod {

    private boolean toggled = false;
    private boolean wasDown = false;

    private Setting keybindingSetting;
    private Setting modeSetting;
    private Setting backgroundSetting;
    private Setting fontColorSetting;

    public ToggleSneakMod() {
        super(
                "ToggleSneak",
                "Allows you to toggle the Sneak button instead of holding it.",
                Type.Mechanic
        );

        keybindingSetting = new Setting("Keybinding", this, Keyboard.KEY_LSHIFT);
        modeSetting = new Setting("Mode", this, "Modern", 0, new String[]{"Modern", "Legacy"});
        backgroundSetting = new Setting("Background", this, true);
        fontColorSetting = new Setting("Font Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0});

        Cloud.INSTANCE.settingManager.addSetting(keybindingSetting);
        Cloud.INSTANCE.settingManager.addSetting(modeSetting);
        Cloud.INSTANCE.settingManager.addSetting(backgroundSetting);
        Cloud.INSTANCE.settingManager.addSetting(fontColorSetting);
    }

    public boolean isSneaking() {
        return toggled;
    }

    // Keep the static accessor for backward compatibility (SneakHud calls this)
    public static boolean isSneakingStatic() {
        try {
            ToggleSneakMod mod = (ToggleSneakMod) Cloud.INSTANCE.modManager.getMod("ToggleSneak");
            return mod != null && mod.isSneaking();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void onDisable(){
        KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) {
        if (toggled) {
            if (!(Cloud.INSTANCE.mc.currentScreen instanceof GuiContainer)) {
                KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
        }
        else {
            KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        boolean isDown = Keyboard.isKeyDown(keybindingSetting.getKey());
        if (isDown && !wasDown) {
            toggled = !toggled;
        }
        wasDown = isDown;
    }
}
