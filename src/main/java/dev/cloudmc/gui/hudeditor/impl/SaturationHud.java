package dev.cloudmc.gui.hudeditor.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.gui.Style;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraft.client.Minecraft;

public class SaturationHud extends HudMod {
    public SaturationHud(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void renderMod(int mouseX, int mouseY) {
        super.renderMod(mouseX, mouseY);
        if (Cloud.INSTANCE.modManager.getMod("Saturation").isToggled() && Minecraft.getMinecraft().thePlayer != null) {
            float saturation = Minecraft.getMinecraft().thePlayer.getFoodStats().getSaturationLevel();
            String text = String.format("Sat: %.1f", saturation);
            int width = Cloud.INSTANCE.fontHelper.size20.getStringWidth(text);
            
            setW(width + 10);
            setH(15);
            
            Helper2D.drawRoundedRectangle(getX(), getY(), getW(), (int) (getH() * getSize()), 2, Style.getColor(100).getRGB(), 0);
            Cloud.INSTANCE.fontHelper.size20.drawString(text, getX() + 5, getY() + 4, -1);
        }
    }
}
