package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;

public class TimeChangerMod extends Mod {

    private static TimeChangerMod instance;

    private Setting offsetSetting;
    private Setting speedSetting;

    public TimeChangerMod() {
        super(
                "TimeChanger",
                "Changes the time of the current World visually.",
                Type.Visual
        );
        instance = this;

        offsetSetting = new Setting("Offset", this, 12000, 0);
        speedSetting = new Setting("Speed", this, 50, 1);

        Cloud.INSTANCE.settingManager.addSetting(offsetSetting);
        Cloud.INSTANCE.settingManager.addSetting(speedSetting);
    }

    // --- Static accessors for WorldMixin ---

    public static float getOffset() {
        return instance != null ? instance.offsetSetting.getCurrentNumber() : 0f;
    }

    public static float getSpeed() {
        return instance != null ? instance.speedSetting.getCurrentNumber() : 1f;
    }
}
