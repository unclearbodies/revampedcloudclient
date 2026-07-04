/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */
package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import java.awt.Color;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ToggleSprintMod extends Mod {

    private boolean toggled = false;
    private boolean wasDown = false;

    private Setting keybindingSetting;
    private Setting modeSetting;
    private Setting backgroundSetting;
    private Setting fontColorSetting;

    private WTapMod cachedWTapMod;
    private boolean wTapLookedUp = false;

    public ToggleSprintMod() {
        super(
                "ToggleSprint",
                "Allows you to toggle the Sprint button instead of holding it.",
                Type.Mechanic
        );

        keybindingSetting = new Setting("Keybinding", this, Keyboard.KEY_LCONTROL);
        modeSetting = new Setting("Mode", this, "Modern", 0, new String[]{"Modern", "Legacy"});
        backgroundSetting = new Setting("Background", this, true);
        fontColorSetting = new Setting("Font Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0});

        Cloud.INSTANCE.settingManager.addSetting(keybindingSetting);
        Cloud.INSTANCE.settingManager.addSetting(modeSetting);
        Cloud.INSTANCE.settingManager.addSetting(backgroundSetting);
        Cloud.INSTANCE.settingManager.addSetting(fontColorSetting);
    }

    private WTapMod getWTapMod() {
        if (!wTapLookedUp) {
            cachedWTapMod = (WTapMod) Cloud.INSTANCE.modManager.getMod("W-Tap");
            wTapLookedUp = true;
        }
        return cachedWTapMod;
    }

    public boolean isSprinting() {
        return toggled;
    }

    @Override
    public void onDisable(){
        KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) {
        WTapMod wTapMod = getWTapMod();
        if (wTapMod != null && wTapMod.isToggled() && wTapMod.isActive()) {
            return;
        }
        KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSprint.getKeyCode(), toggled);
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
