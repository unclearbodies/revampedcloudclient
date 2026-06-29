package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class CustomSkyMod extends Mod {
    
    private Setting skyColorSetting;

    public CustomSkyMod() {
        super("Custom Sky", "Changes the color of the sky/fog.", Type.Visual);
        
        skyColorSetting = new Setting("Sky Color", this, new Color(150, 200, 255), new Color(150, 200, 255), 0, new float[]{0, 0});
        Cloud.INSTANCE.settingManager.addSetting(skyColorSetting);
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        Color c = skyColorSetting.getColor();
        event.red = c.getRed() / 255f;
        event.green = c.getGreen() / 255f;
        event.blue = c.getBlue() / 255f;
    }
}
