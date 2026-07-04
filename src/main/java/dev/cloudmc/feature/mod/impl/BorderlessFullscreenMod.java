package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

public class BorderlessFullscreenMod extends Mod {

    public BorderlessFullscreenMod() {
        super(
                "Borderless Fullscreen",
                "Runs the game in a borderless window, making it easier to alt-tab.",
                Type.Tweaks
        );
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isFullScreen()) {
            mc.toggleFullscreen(); // Turn off vanilla fullscreen first
        }
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        try {
            Display.setFullscreen(false);
            Display.setResizable(false);
            Display.setLocation(0, 0);
            
            // Get screen dimensions
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(screenSize.width, screenSize.height));
            
            mc.resize(screenSize.width, screenSize.height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
        try {
            Minecraft mc = Minecraft.getMinecraft();
            Display.setResizable(true);
            Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(854, 480));
            Display.setLocation(Display.getDisplayMode().getWidth() / 2, Display.getDisplayMode().getHeight() / 2);
            mc.resize(854, 480);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
