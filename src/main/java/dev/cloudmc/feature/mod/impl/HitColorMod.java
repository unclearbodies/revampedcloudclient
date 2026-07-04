package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;

import java.awt.*;

public class HitColorMod extends Mod {

    private static HitColorMod instance;

    private Setting damageColorSetting;
    private Setting alphaSetting;

    public HitColorMod() {
        super(
                "Hit Color",
                "Changes the color of damaged entities.",
                Type.Visual
        );
        instance = this;

        damageColorSetting = new Setting("Damage Color", this, new Color(255, 0, 0), new Color(255, 255, 255), 0, new float[]{145, 0});
        alphaSetting = new Setting("Alpha", this, 255, 80);

        Cloud.INSTANCE.settingManager.addSetting(damageColorSetting);
        Cloud.INSTANCE.settingManager.addSetting(alphaSetting);
    }

    // --- Static accessors for RendererLivingEntityMixin ---

    public static Color getDamageColor() {
        return instance != null ? instance.damageColorSetting.getColor() : new Color(255, 0, 0);
    }

    public static float getAlpha() {
        return instance != null ? instance.alphaSetting.getCurrentNumber() / 255f : 0.3f;
    }
}
