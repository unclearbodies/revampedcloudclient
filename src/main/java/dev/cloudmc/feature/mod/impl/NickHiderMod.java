/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;

public class NickHiderMod extends Mod {

    private static NickHiderMod instance;
    private Setting nicknameSetting;

    public NickHiderMod() {
        super(
                "NickHider",
                "Hides your nickname in game by replacing it.",
                Type.Visual
        );
        instance = this;

        nicknameSetting = new Setting("Nickname", this, "Name", "You", 3);
        Cloud.INSTANCE.settingManager.addSetting(nicknameSetting);
    }

    public static String replaceNickname(String nick) {
        if (instance == null || Cloud.INSTANCE == null || Cloud.INSTANCE.modManager == null || !Cloud.INSTANCE.modManager.isModToggled("NickHider")) {
            return nick;
        }
        String replacement = instance.nicknameSetting.getText();
        if (replacement == null || replacement.isEmpty()) {
            return nick;
        }
        return nick.replace(
                Cloud.INSTANCE.mc.getSession().getUsername(),
                replacement
        );
    }
}
