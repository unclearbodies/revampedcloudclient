/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.helpers.animation.Animate;
import dev.cloudmc.helpers.animation.Easing;
import dev.cloudmc.helpers.hud.ScrollHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ZoomMod extends Mod {

    private static ZoomMod instance;

    private final Animate animate = new Animate();
    private final ScrollHelper scrollHelper = new ScrollHelper(0, 0, 5, 50);
    private boolean zoom = false;

    private Setting keybindingSetting;
    private Setting zoomAmountSetting;
    private Setting smoothZoomSetting;

    public ZoomMod() {
        super(
                "Zoom",
                "Allows you to zoom into the world.",
                Type.Mechanic
        );
        instance = this;

        keybindingSetting = new Setting("Keybinding", this, Keyboard.KEY_C);
        zoomAmountSetting = new Setting("Zoom Amount", this, 100, 30);
        smoothZoomSetting = new Setting("Smooth Zoom", this, true);

        Cloud.INSTANCE.settingManager.addSetting(keybindingSetting);
        Cloud.INSTANCE.settingManager.addSetting(zoomAmountSetting);
        Cloud.INSTANCE.settingManager.addSetting(smoothZoomSetting);

        animate.setEase(Easing.LINEAR).setSpeed(700);
    }

    public static ZoomMod getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text e) {
        animate.setMin(getAmount() / 2).setMax(Cloud.INSTANCE.mc.gameSettings.fovSetting).update();
        scrollHelper.setMinScroll(isSmooth() ? animate.getValueI() - 5 : getAmount() - 5);
        scrollHelper.update();

        if (zoom && Cloud.INSTANCE.mc.currentScreen == null) {
            scrollHelper.updateScroll();
        } else {
            scrollHelper.setScrollStep(0);
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        zoom = Keyboard.isKeyDown(getKey());
        animate.setReversed(zoom);
    }

    public float getFOV() {
        if (isSmooth()) {
            return animate.getValueI() - scrollHelper.getCalculatedScroll();
        }
        return zoom ? getAmount() - scrollHelper.getCalculatedScroll() : Cloud.INSTANCE.mc.gameSettings.fovSetting;
    }

    public boolean isZoom() {
        return zoom;
    }

    private boolean isSmooth() {
        return smoothZoomSetting.isCheckToggled();
    }

    private float getAmount() {
        return zoomAmountSetting.getCurrentNumber();
    }

    private int getKey() {
        return keybindingSetting.getKey();
    }
}
