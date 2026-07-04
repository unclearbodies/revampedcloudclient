package dev.cloudmc.gui.modmenu.impl.sidebar.mods.type;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.gui.Style;
import dev.cloudmc.gui.modmenu.impl.sidebar.mods.Button;
import dev.cloudmc.gui.modmenu.impl.sidebar.mods.Settings;
import dev.cloudmc.helpers.render.Helper2D;
import dev.cloudmc.helpers.MathHelper;

public class ActionButton extends Settings {

    public ActionButton(Setting setting, Button button, int y) {
        super(setting, button, y);
    }

    @Override
    public void renderSetting(int mouseX, int mouseY) {
        boolean roundedCorners = Cloud.INSTANCE.optionManager.getOptionByName("Rounded Corners").isCheckToggled();
        int color = Cloud.INSTANCE.optionManager.getOptionByName("Color").getColor().getRGB();

        Cloud.INSTANCE.fontHelper.size30.drawString(
                setting.getName(),
                button.getPanel().getX() + 20,
                button.getPanel().getY() + button.getPanel().getH() + getY() + 6,
                color
        );

        boolean hovered = MathHelper.withinBox(
                button.getPanel().getX() + button.getPanel().getW() - 40,
                button.getPanel().getY() + button.getPanel().getH() + getY() + 2,
                20, 20, mouseX, mouseY);

        Helper2D.drawRoundedRectangle(
                button.getPanel().getX() + button.getPanel().getW() - 40,
                button.getPanel().getY() + button.getPanel().getH() + getY() + 2,
                20, 20, 2,
                Style.getColor(hovered ? 80 : 50).getRGB(),
                roundedCorners ? 0 : -1
        );
        
        Helper2D.drawPicture(
                button.getPanel().getX() + button.getPanel().getW() - 36,
                button.getPanel().getY() + button.getPanel().getH() + 6 + getY(),
                12,
                12,
                color,
                "icon/check.png"
        );
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0){
            if (MathHelper.withinBox(
                    button.getPanel().getX() + button.getPanel().getW() - 40,
                    button.getPanel().getY() + button.getPanel().getH() + 2 + getY(),
                    20, 20, mouseX, mouseY)
            ) {
                if (setting.getAction() != null) {
                    setting.getAction().run();
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {}

    @Override
    public void keyTyped(char typedChar, int keyCode) {}
}
