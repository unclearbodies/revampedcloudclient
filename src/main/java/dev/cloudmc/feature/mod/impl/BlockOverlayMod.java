/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.helpers.render.Helper3D;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BlockOverlayMod extends Mod {

    private Setting outlineSetting;
    private Setting fillingSetting;
    private Setting thicknessSetting;
    private Setting outlineColorSetting;
    private Setting fillingColorSetting;
    private Setting alphaSetting;

    public BlockOverlayMod() {
        super(
                "BlockOverlay",
                "Adds an customizable overlay to blocks.",
                Type.Visual
        );

        outlineSetting = new Setting("Outline", this, true);
        fillingSetting = new Setting("Filling", this, true);
        thicknessSetting = new Setting("Thickness", this, 20, 3);
        outlineColorSetting = new Setting("Outline Color", this, new Color(0, 0, 0), new Color(255, 0, 0), 0, new float[]{0, 65});
        fillingColorSetting = new Setting("Filling Color", this, new Color(0, 0, 0), new Color(255, 0, 0), 0, new float[]{0, 65});
        alphaSetting = new Setting("Alpha", this, 255, 100);

        Cloud.INSTANCE.settingManager.addSetting(outlineSetting);
        Cloud.INSTANCE.settingManager.addSetting(fillingSetting);
        Cloud.INSTANCE.settingManager.addSetting(thicknessSetting);
        Cloud.INSTANCE.settingManager.addSetting(outlineColorSetting);
        Cloud.INSTANCE.settingManager.addSetting(fillingColorSetting);
        Cloud.INSTANCE.settingManager.addSetting(alphaSetting);
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
        e.setCanceled(true);
        drawSelectionBox(e.player, e.target, e.subID, e.partialTicks);
    }

    public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks) {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GL11.glLineWidth(thicknessSetting.getCurrentNumber());
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            BlockPos blockpos = movingObjectPositionIn.getBlockPos();
            Block block = Cloud.INSTANCE.mc.theWorld.getBlockState(blockpos).getBlock();

            if (block.getMaterial() != Material.air && Cloud.INSTANCE.mc.theWorld.getWorldBorder().contains(blockpos)) {
                block.setBlockBoundsBasedOnState(Cloud.INSTANCE.mc.theWorld, blockpos);
                double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
                double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
                double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

                double var = 0.002F;
                AxisAlignedBB box =
                        block.getSelectedBoundingBox(Cloud.INSTANCE.mc.theWorld, blockpos)
                                .expand(var, var, var)
                                .offset(-d0, -d1, -d2);

                if (fillingSetting.isCheckToggled()) {
                    Color fc = fillingColorSetting.getColor();
                    GlStateManager.color(
                            fc.getRed() / 255f,
                            fc.getGreen() / 255f,
                            fc.getBlue() / 255f,
                            alphaSetting.getCurrentNumber() / 255f
                    );
                    Helper3D.drawFilledBoundingBox(box);
                }
                if (outlineSetting.isCheckToggled()) {
                    Color oc = outlineColorSetting.getColor();
                    GlStateManager.color(
                            oc.getRed() / 255f,
                            oc.getGreen() / 255f,
                            oc.getBlue() / 255f,
                            alphaSetting.getCurrentNumber() / 255f
                    );
                    RenderGlobal.drawSelectionBoundingBox(box);
                }
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
