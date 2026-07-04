/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.gui.hudeditor.impl.keystrokes.keys;

import dev.cloudmc.Cloud;
import dev.cloudmc.helpers.render.Helper2D;
import dev.cloudmc.helpers.animation.Animate;
import dev.cloudmc.helpers.animation.Easing;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class MouseKey {

    private Animate animate = new Animate();
    private int lastCps = -1;
    private String lastCpsString = "";

    public MouseKey() {
        animate.setEase(Easing.CUBIC_IN).setMin(0).setMax(100).setSpeed(1000);
    }

    private String getLabel(int mouseButton, boolean cps) {
        int currentCps = getCPS(mouseButton);
        if (currentCps != 0 && cps) {
            if (currentCps != lastCps) {
                lastCps = currentCps;
                lastCpsString = currentCps + " CPS";
            }
            return lastCpsString;
        }
        return mouseButton == 0 ? "LMB" : "RMB";
    }

    public void renderKey(int x, int y, int width, int height, boolean modern, int mouseButton, int color, int fontColor, boolean background, boolean cps) {
        boolean mouseDown;
        if(Cloud.INSTANCE.mc.currentScreen == null) {
            mouseDown = Mouse.isButtonDown(mouseButton);
        }
        else {
            mouseDown = false;
        }

        animate.setReversed(mouseDown).update();

        if (modern) {
            if (background) {
                Helper2D.drawRoundedRectangle(x, y, width, height, 2, color, 0);
            }

            if (mouseDown || !animate.hasFinished()) {
                int alpha = 100 - animate.getValueI();
                Helper2D.drawRoundedRectangle(x, y, width, height, 2, (alpha << 24) | 0xFFFFFF, 0);
            }

            String label = getLabel(mouseButton, cps);
            Cloud.INSTANCE.fontHelper.size20.drawString(
                    label,
                    x - Cloud.INSTANCE.fontHelper.size20.getStringWidth(label) / 2f + width / 2f,
                    y + height / 2f - 4,
                    fontColor
            );
        }
        else {
            if (background) {
                Helper2D.drawRectangle(x, y, width, height, color);
            }

            if (mouseDown) {
                int alpha = 100 - animate.getValueI();
                Helper2D.drawRectangle(x, y, width, height, (alpha << 24) | 0xFFFFFF);
            }

            String label = getLabel(mouseButton, cps);
            Cloud.INSTANCE.mc.fontRendererObj.drawString(
                    label,
                    x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(label) / 2 + width / 2,
                    y + height / 2 - 4,
                    fontColor
            );
        }
    }

    private int getCPS(int mouseButton) {
        return Cloud.INSTANCE.cpsHelper.getCPS(mouseButton);
    }
}
