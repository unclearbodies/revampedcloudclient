package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.renderer.entity.RenderManager;

public class HitboxOnlyMod extends Mod {

    private Setting outlineColorSetting;
    private Setting lineWidthSetting;
    private Setting renderFillSetting;
    private Setting fillOpacitySetting;
    private Setting fillColorSetting;

    public HitboxOnlyMod() {
        super(
                "HitboxOnly",
                "Renders a wireframe hitbox overlay on players.",
                Type.Visual
        );

        outlineColorSetting = new Setting("Outline Color", this, new Color(255, 0, 0), new Color(255, 0, 0), 0, new float[]{0, 0});
        lineWidthSetting = new Setting("Line Width", this, 1.0f, 5.0f, 2.0f);
        renderFillSetting = new Setting("Render Fill", this, true);
        fillOpacitySetting = new Setting("Fill Opacity", this, 0.0f, 1.0f, 0.0f);
        fillColorSetting = new Setting("Fill Color", this, new Color(0, 0, 0), new Color(0, 0, 0), 0, new float[]{0, 0});

        Cloud.INSTANCE.settingManager.addSetting(outlineColorSetting);
        Cloud.INSTANCE.settingManager.addSetting(lineWidthSetting);
        Cloud.INSTANCE.settingManager.addSetting(renderFillSetting);
        Cloud.INSTANCE.settingManager.addSetting(fillOpacitySetting);
        Cloud.INSTANCE.settingManager.addSetting(fillColorSetting);
    }

    /**
     * Renders the hitbox wireframe AFTER the player model has already been drawn,
     * so the player model is visible with the hitbox overlaid on top.
     */
    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (!isToggled()) return;

        EntityPlayer player = event.entityPlayer;
        if (player == Minecraft.getMinecraft().thePlayer) return;

        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialRenderTick - renderManager.viewerPosX;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialRenderTick - renderManager.viewerPosY;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialRenderTick - renderManager.viewerPosZ;

        AxisAlignedBB bbox = player.getEntityBoundingBox().offset(-player.posX, -player.posY, -player.posZ).offset(x, y, z);

        Color outlineColor = outlineColorSetting.getColor();
        float lineWidth = lineWidthSetting.getCurrentNumber();
        boolean renderFill = renderFillSetting.isCheckToggled();
        float fillOpacity = fillOpacitySetting.getCurrentNumber();
        Color fillColor = fillColorSetting.getColor();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        if (renderFill && fillOpacity > 0.0f) {
            GlStateManager.color(fillColor.getRed() / 255.0f, fillColor.getGreen() / 255.0f, fillColor.getBlue() / 255.0f, fillOpacity);
            drawFilledBox(bbox);
        }

        GlStateManager.color(outlineColor.getRed() / 255.0f, outlineColor.getGreen() / 255.0f, outlineColor.getBlue() / 255.0f, 1.0f);
        GL11.glLineWidth(lineWidth);
        RenderGlobal.drawSelectionBoundingBox(bbox);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawFilledBox(AxisAlignedBB bb) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();

        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();

        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        worldRenderer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        worldRenderer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();

        tessellator.draw();
    }
}
