/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.gui.hudeditor.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.gui.Style;
import dev.cloudmc.gui.hudeditor.HudEditor;
import dev.cloudmc.gui.hudeditor.impl.HudMod;
import dev.cloudmc.helpers.render.GLHelper;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FpsHud extends HudMod {

    private static int frames = 0;
    private static int fps = 0;
    private static long lastUpdate = 0;

    private Setting modeSetting;
    private Setting backgroundSetting;
    private Setting fontColorSetting;
    private Setting rawFpsSetting;

    public FpsHud(String name, int x, int y) {
        super(name, x, y);
        setW(60);
        setH(20);
        
        modeSetting = Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Mode");
        backgroundSetting = Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Background");
        fontColorSetting = Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Font Color");
        rawFpsSetting = Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Raw FPS");
    }

    private void updateRawFps() {
        frames++;
        if (System.currentTimeMillis() - lastUpdate > 1000) {
            fps = frames;
            frames = 0;
            lastUpdate = System.currentTimeMillis();
        }
    }

    private String getFpsText() {
        if (rawFpsSetting.isCheckToggled()) {
            return "FPS: " + fps;
        }
        return "FPS: " + Minecraft.getDebugFPS();
    }

    @Override
    public void renderMod(int mouseX, int mouseY) {
        GLHelper.startScale(getX(), getY(), getSize());
        if (Cloud.INSTANCE.modManager.getMod(getName()).isToggled()) {
            String text = getFpsText();
            if (isModern()) {
                if (isBackground()) {
                    Helper2D.drawRoundedRectangle(getX(), getY(), getW(), getH(), 2, Style.getColor(50).getRGB(), 0);
                }
                Cloud.INSTANCE.fontHelper.size20.drawString(
                        text,
                        getX() + getW() / 2f - Cloud.INSTANCE.fontHelper.size20.getStringWidth(text) / 2f,
                        getY() + 6,
                        getColor()
                );
            } else {
                if (isBackground()) {
                    Helper2D.drawRectangle(getX(), getY(), getW(), getH(), Style.getColor(50).getRGB());
                }
                Cloud.INSTANCE.mc.fontRendererObj.drawString(
                        text,
                        getX() + getW() / 2 - Cloud.INSTANCE.mc.fontRendererObj.getStringWidth(text) / 2,
                        getY() + 6,
                        getColor()
                );
            }

            super.renderMod(mouseX, mouseY);
        }
        GLHelper.endScale();
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Pre.Text e) {
        updateRawFps();
        GLHelper.startScale(getX(), getY(), getSize());
        if (Cloud.INSTANCE.modManager.getMod(getName()).isToggled() && !(Cloud.INSTANCE.mc.currentScreen instanceof HudEditor)) {
            String text = getFpsText();
            if (isModern()) {
                if (isBackground()) {
                    Helper2D.drawRoundedRectangle(getX(), getY(), getW(), getH(), 2, 0x50000000, 0);
                }
                Cloud.INSTANCE.fontHelper.size20.drawString(
                        text,
                        getX() + getW() / 2f - Cloud.INSTANCE.fontHelper.size20.getStringWidth(text) / 2f,
                        getY() + 6,
                        getColor()
                );
            } else {
                if (isBackground()) {
                    Helper2D.drawRectangle(getX(), getY(), getW(), getH(), 0x50000000);
                }
                Cloud.INSTANCE.mc.fontRendererObj.drawString(
                        text,
                        getX() + getW() / 2 - Cloud.INSTANCE.mc.fontRendererObj.getStringWidth(text) / 2,
                        getY() + 6,
                        getColor()
                );
            }
        }
        GLHelper.endScale();
    }

    private int getColor() {
        return fontColorSetting.getColor().getRGB();
    }

    private boolean isModern() {
        return modeSetting.getCurrentMode().equalsIgnoreCase("Modern");
    }

    private boolean isBackground() {
        return backgroundSetting.isCheckToggled();
    }
}
