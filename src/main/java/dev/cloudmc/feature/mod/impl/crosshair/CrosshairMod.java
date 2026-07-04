/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl.crosshair;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.helpers.ResolutionHelper;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class CrosshairMod extends Mod {

    public static final LayoutManager layoutManager = new LayoutManager();

    private Setting colorSetting;
    private Setting cellsSetting;

    public CrosshairMod() {
        super(
                "Crosshair",
                "Makes Crosshair customizable.",
                Type.Hud
        );

        colorSetting = new Setting("Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0});
        cellsSetting = new Setting("Cells", this, layoutManager.getLayout(0));

        Cloud.INSTANCE.settingManager.addSetting(colorSetting);
        Cloud.INSTANCE.settingManager.addSetting(cellsSetting);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        if (e.type == RenderGameOverlayEvent.ElementType.TEXT) {
            boolean[][] cells = cellsSetting.getCells();
            int color = colorSetting.getColor().getRGB();
            int centerX = ResolutionHelper.getWidth() / 2 - 5;
            int centerY = ResolutionHelper.getHeight() / 2 - 5;
            for (int row = 0; row < 11; row++) {
                for (int col = 0; col < 11; col++) {
                    if (cells[row][col] && isToggled()) {
                        Helper2D.drawRectangle(
                                centerX + col,
                                centerY + row,
                                1, 1, color
                        );
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Pre e) {
        if (e.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (!e.isCanceled() && e.isCancelable()) {
                e.setCanceled(true);
            }
        }
    }
}
