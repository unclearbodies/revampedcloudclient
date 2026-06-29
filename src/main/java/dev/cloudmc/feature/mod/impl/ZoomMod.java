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

    private static final Animate animate = new Animate();
    private static final ScrollHelper scrollHelper = new ScrollHelper(0, 0, 5, 50);
    private static boolean zoom = false;

    private static Setting keybindingSetting;
    private static Setting zoomAmountSetting;
    private static Setting smoothZoomSetting;

    public ZoomMod() {
        super(
                "Zoom",
                "Allows you to zoom into the world.",
                Type.Mechanic
        );

        keybindingSetting = new Setting("Keybinding", this, Keyboard.KEY_C);
        zoomAmountSetting = new Setting("Zoom Amount", this, 100, 30);
        smoothZoomSetting = new Setting("Smooth Zoom", this, true);

        Cloud.INSTANCE.settingManager.addSetting(keybindingSetting);
        Cloud.INSTANCE.settingManager.addSetting(zoomAmountSetting);
        Cloud.INSTANCE.settingManager.addSetting(smoothZoomSetting);

        animate.setEase(Easing.LINEAR).setSpeed(700);
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

    public static float getFOV() {
        if (isSmooth()) {
            return animate.getValueI() - scrollHelper.getCalculatedScroll();
        }
        return zoom ? getAmount() - scrollHelper.getCalculatedScroll() : Cloud.INSTANCE.mc.gameSettings.fovSetting;
    }

    public static boolean isZoom() {
        return zoom;
    }

    private static boolean isSmooth() {
        return smoothZoomSetting.isCheckToggled();
    }

    private static float getAmount() {
        return zoomAmountSetting.getCurrentNumber();
    }

    private int getKey() {
        return keybindingSetting.getKey();
    }
}
