package first.rain.anticheat.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Immediate-mode GL11 shape helpers for GUI rendering.
 * Every public method fully sets up and restores GL state (blend, texture,
 * alpha test, cull, color), so it is safe to call between vanilla draws.
 */
public final class RenderUtil {
   private static final int CORNER_SEGMENTS = 8;

   private RenderUtil() {
   }

   /**
    * Filled rounded rectangle drawn as a single triangle fan: center vertex,
    * then the perimeter (four 90-degree corner arcs). One fan means shared
    * edges between triangles, so there are no seam artifacts.
    *
    * @param color ARGB, e.g. 0xFF101010
    */
   public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
      float r = clampRadius(radius, width, height);
      setupShapeState(color);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
      worldrenderer.pos(x + width / 2.0F, y + height / 2.0F, 0).endVertex();
      emitRoundedPerimeter(worldrenderer, x, y, width, height, r);
      worldrenderer.pos(x, y + r, 0).endVertex(); // repeat the first perimeter vertex to close the fan
      tessellator.draw();
      restoreShapeState();
   }

   /**
    * Rounded rectangle outline as an antialiased line loop.
    */
   public static void drawRoundedOutline(float x, float y, float width, float height, float radius, float lineWidth, int color) {
      float r = clampRadius(radius, width, height);
      setupShapeState(color);
      GL11.glEnable(GL11.GL_LINE_SMOOTH);
      GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
      GL11.glLineWidth(lineWidth);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
      emitRoundedPerimeter(worldrenderer, x, y, width, height, r);
      tessellator.draw();
      GL11.glLineWidth(1.0F);
      GL11.glDisable(GL11.GL_LINE_SMOOTH);
      restoreShapeState();
   }

   /**
    * Filled circle (triangle fan). Used for toggle knobs.
    */
   public static void drawCircle(float centerX, float centerY, float radius, int color) {
      setupShapeState(color);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
      worldrenderer.pos(centerX, centerY, 0).endVertex();
      int segments = CORNER_SEGMENTS * 4;
      for (int i = 0; i <= segments; ++i) {
         double angle = Math.PI * 2.0D * i / segments;
         worldrenderer.pos(centerX + net.minecraft.util.MathHelper.cos((float) angle) * radius, centerY + net.minecraft.util.MathHelper.sin((float) angle) * radius, 0).endVertex();
      }
      tessellator.draw();
      restoreShapeState();
   }

   /**
    * Filled upright triangle centered on the supplied point.
    */
   public static void drawTriangle(float centerX, float centerY, float width, float height, int color) {
      float halfWidth = width / 2.0F;
      float halfHeight = height / 2.0F;
      setupShapeState(color);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
      worldrenderer.pos(centerX, centerY - halfHeight, 0).endVertex();
      worldrenderer.pos(centerX - halfWidth, centerY + halfHeight, 0).endVertex();
      worldrenderer.pos(centerX + halfWidth, centerY + halfHeight, 0).endVertex();
      tessellator.draw();
      restoreShapeState();
   }

   /**
    * Upright triangle outline, matching drawTriangle's bounds.
    */
   public static void drawTriangleOutline(float centerX, float centerY, float width, float height, float lineWidth, int color) {
      float halfWidth = width / 2.0F;
      float halfHeight = height / 2.0F;
      setupShapeState(color);
      GL11.glEnable(GL11.GL_LINE_SMOOTH);
      GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
      GL11.glLineWidth(lineWidth);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
      worldrenderer.pos(centerX, centerY - halfHeight, 0).endVertex();
      worldrenderer.pos(centerX - halfWidth, centerY + halfHeight, 0).endVertex();
      worldrenderer.pos(centerX + halfWidth, centerY + halfHeight, 0).endVertex();
      tessellator.draw();
      GL11.glLineWidth(1.0F);
      GL11.glDisable(GL11.GL_LINE_SMOOTH);
      restoreShapeState();
   }

   /**
    * Per-channel ARGB interpolation, t clamped to [0, 1].
    */
   public static int lerpColor(int from, int to, float t) {
      float clamped = t < 0.0F ? 0.0F : (t > 1.0F ? 1.0F : t);
      int a = lerpChannel(from >>> 24 & 0xFF, to >>> 24 & 0xFF, clamped);
      int r = lerpChannel(from >>> 16 & 0xFF, to >>> 16 & 0xFF, clamped);
      int g = lerpChannel(from >>> 8 & 0xFF, to >>> 8 & 0xFF, clamped);
      int b = lerpChannel(from & 0xFF, to & 0xFF, clamped);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int lerpChannel(int from, int to, float t) {
      return (int)((float)from + (float)(to - from) * t);
   }

   /**
    * Multiplies a color's alpha channel by mult (clamped to [0, 1]).
    */
   public static int applyAlpha(int color, float mult) {
      float clamped = mult < 0.0F ? 0.0F : (mult > 1.0F ? 1.0F : mult);
      int alpha = (int)((float)(color >>> 24 & 0xFF) * clamped);
      return alpha << 24 | color & 0xFFFFFF;
   }

   /**
    * Soft drop shadow: stacked translucent black rounded rects expanding
    * outward, nudged 2px down so the light reads as coming from above.
    */
   public static void drawRoundedShadow(float x, float y, float width, float height, float radius) {
      int layers = 6;
      for (int i = layers; i >= 1; --i) {
         int alpha = (int)(26.0F * (1.0F - (float)(i - 1) / (float)layers));
         if (alpha > 0) {
            drawRoundedRect(x - i, y - i + 2, width + i * 2, height + i * 2, radius + i, alpha << 24);
         }
      }
   }

   /**
    * Animated monochrome toggle pill shared by the GUI cards.
    *
    * @param knobAnim  0 = fully off, 1 = fully on
    * @param alphaMult page fade multiplier
    */
   public static void drawTogglePill(float x, float y, float width, float height, float knobAnim, float alphaMult) {
      int track = applyAlpha(lerpColor(0xFF1F1F1F, 0xFFF2F2F2, knobAnim), alphaMult);
      drawRoundedRect(x, y, width, height, height / 2.0F, track);
      int outlineAlpha = (int)(110.0F * (1.0F - knobAnim) * alphaMult);
      if (outlineAlpha > 3) {
         drawRoundedOutline(x, y, width, height, height / 2.0F, 1.0F, outlineAlpha << 24 | 0xFFFFFF);
      }
      float knobRadius = height / 2.0F - 2.0F;
      float knobMin = x + 2.0F + knobRadius;
      float knobMax = x + width - 2.0F - knobRadius;
      float knobX = knobMin + (knobMax - knobMin) * knobAnim;
      int knobColor = applyAlpha(lerpColor(0xFFB4B4B4, 0xFF0D0D0D, knobAnim), alphaMult);
      drawCircle(knobX, y + height / 2.0F, knobRadius, knobColor);
   }

   private static float clampRadius(float radius, float width, float height) {
      float max = Math.min(width, height) / 2.0F;
      if (radius < 0.0F) {
         return 0.0F;
      }
      return Math.min(radius, max);
   }

   /**
    * Walks the outline clockwise starting at the left edge of the top-left
    * corner. Screen space has +y pointing down, so angle 180 = left,
    * 270 = up, 0 = right, 90 = down.
    */
   private static void emitRoundedPerimeter(WorldRenderer worldrenderer, float x, float y, float width, float height, float r) {
      emitArc(worldrenderer, x + r, y + r, r, 180.0F);                   // top-left
      emitArc(worldrenderer, x + width - r, y + r, r, 270.0F);           // top-right
      emitArc(worldrenderer, x + width - r, y + height - r, r, 0.0F);    // bottom-right
      emitArc(worldrenderer, x + r, y + height - r, r, 90.0F);           // bottom-left
   }

   private static void emitArc(WorldRenderer worldrenderer, float centerX, float centerY, float r, float startAngleDeg) {
      for (int i = 0; i <= CORNER_SEGMENTS; ++i) {
         double angle = Math.toRadians(startAngleDeg + 90.0D * i / CORNER_SEGMENTS);
         worldrenderer.pos(centerX + net.minecraft.util.MathHelper.cos((float) angle) * r, centerY + net.minecraft.util.MathHelper.sin((float) angle) * r, 0).endVertex();
      }
   }

   private static void setupShapeState(int color) {
      float alpha = (color >>> 24 & 0xFF) / 255.0F;
      float red = (color >>> 16 & 0xFF) / 255.0F;
      float green = (color >>> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      GlStateManager.enableBlend();                        // enableBlend
      GlStateManager.disableTexture2D();                        // disableTexture2D
      GlStateManager.disableAlpha();                        // disableAlpha — the GUI alpha test would discard low-alpha fragments
      GlStateManager.disableCull();                        // disableCull — fan winding must not matter
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);          // tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO)
      GlStateManager.color(red, green, blue, alpha); // color
   }

   private static void restoreShapeState() {
      GlStateManager.enableCull();                     // enableCull
      GlStateManager.enableAlpha();                     // enableAlpha
      GlStateManager.enableTexture2D();                     // enableTexture2D
      GlStateManager.disableBlend();                     // disableBlend
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // reset color so following font/texture draws are untinted
   }
}
