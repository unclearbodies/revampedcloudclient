package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;

import java.awt.*;

public class NameTagMod extends Mod {

    private static NameTagMod instance;

    private Setting thirdPersonSetting;
    private Setting opacitySetting;
    private Setting sizeSetting;
    private Setting yPositionSetting;
    private Setting disablePlayerNameTagsSetting;
    private Setting fontColorSetting;

    public NameTagMod() {
        super(
                "NameTag",
                "Adds tweaks to NameTags.",
                Type.Tweaks
        );
        instance = this;

        thirdPersonSetting = new Setting("NameTag in 3rd Person", this, true);
        opacitySetting = new Setting("Opacity", this, 255, 64);
        sizeSetting = new Setting("Size", this, 3, 1);
        yPositionSetting = new Setting("Y Position", this, 5, 2.5f);
        disablePlayerNameTagsSetting = new Setting("Disable Player NameTags", this, false);
        fontColorSetting = new Setting("Font Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0});

        Cloud.INSTANCE.settingManager.addSetting(thirdPersonSetting);
        Cloud.INSTANCE.settingManager.addSetting(opacitySetting);
        Cloud.INSTANCE.settingManager.addSetting(sizeSetting);
        Cloud.INSTANCE.settingManager.addSetting(yPositionSetting);
        Cloud.INSTANCE.settingManager.addSetting(disablePlayerNameTagsSetting);
        Cloud.INSTANCE.settingManager.addSetting(fontColorSetting);
    }

    // --- Static accessors for mixins (avoid per-frame SettingManager string lookups) ---

    public static int getFontColorRGB() {
        return instance != null ? instance.fontColorSetting.getColor().getRGB() : 0xFFFFFF;
    }

    public static float getOpacity() {
        return instance != null ? instance.opacitySetting.getCurrentNumber() / 255f : 0.25f;
    }

    public static float getSize() {
        return instance != null ? instance.sizeSetting.getCurrentNumber() : 1f;
    }

    public static float getYPosition() {
        return instance != null ? instance.yPositionSetting.getCurrentNumber() - 2.5f : 0f;
    }

    public static boolean isThirdPersonEnabled() {
        return instance != null && instance.thirdPersonSetting.isCheckToggled();
    }

    public static boolean isDisablePlayerNameTagsEnabled() {
        return instance != null && instance.disablePlayerNameTagsSetting.isCheckToggled();
    }
}
