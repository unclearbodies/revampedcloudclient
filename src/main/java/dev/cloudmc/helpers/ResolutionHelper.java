package dev.cloudmc.helpers;

import dev.cloudmc.Cloud;
import net.minecraft.client.gui.ScaledResolution;

public class ResolutionHelper {

    private static ScaledResolution scaledResolution;
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static int lastScale = -1;

    private static void update() {
        if (Cloud.INSTANCE.mc == null || Cloud.INSTANCE.mc.gameSettings == null) return;
        int width = Cloud.INSTANCE.mc.displayWidth;
        int height = Cloud.INSTANCE.mc.displayHeight;
        int scale = Cloud.INSTANCE.mc.gameSettings.guiScale;
        
        if (scaledResolution == null || width != lastWidth || height != lastHeight || scale != lastScale) {
            scaledResolution = new ScaledResolution(Cloud.INSTANCE.mc);
            lastWidth = width;
            lastHeight = height;
            lastScale = scale;
        }
    }

    public static int getHeight() {
        update();
        return scaledResolution != null ? scaledResolution.getScaledHeight() : 0;
    }

    public static int getWidth() {
        update();
        return scaledResolution != null ? scaledResolution.getScaledWidth() : 0;
    }

    public static int getFactor() {
        update();
        return scaledResolution != null ? scaledResolution.getScaleFactor() : 1;
    }
}