package dev.cloudmc.mixins;

import dev.cloudmc.Cloud;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.FloatBuffer;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin<T extends EntityLivingBase> extends Render<T> {

    protected RendererLivingEntityMixin(RenderManager renderManager) {
        super(renderManager);
    }

    @Redirect(
            method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"
            )
    )
    public Entity canRenderName(RenderManager instance) {
        if (dev.cloudmc.feature.mod.impl.NameTagMod.isThirdPersonEnabled()) {
            return null;
        }

        return instance.livingPlayer;
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    public void canRenderName(T entity, CallbackInfoReturnable<Boolean> cir) {
        if(dev.cloudmc.feature.mod.impl.NameTagMod.isDisablePlayerNameTagsEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 0, remap = false))
    public FloatBuffer setRed(FloatBuffer instance, float v) {
        if (Cloud.INSTANCE.modManager.isModToggled("Hit Color")) {
            instance.put(dev.cloudmc.feature.mod.impl.HitColorMod.getDamageColor().getRed() / 255f);
        } else {
            instance.put(1f);
        }
        return instance;
    }

    @Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 1, remap = false))
    public FloatBuffer setGreen(FloatBuffer instance, float v) {
        if (Cloud.INSTANCE.modManager.isModToggled("Hit Color")) {
            instance.put(dev.cloudmc.feature.mod.impl.HitColorMod.getDamageColor().getGreen() / 255f);
        } else {
            instance.put(0f);
        }
        return instance;
    }

    @Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 2, remap = false))
    public FloatBuffer setBlue(FloatBuffer instance, float v) {
        if (Cloud.INSTANCE.modManager.isModToggled("Hit Color")) {
            instance.put(dev.cloudmc.feature.mod.impl.HitColorMod.getDamageColor().getBlue() / 255f);
        } else {
            instance.put(0f);
        }
        return instance;
    }

    @Redirect(method = "setBrightness", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 3, remap = false))
    public FloatBuffer setAlpha(FloatBuffer instance, float v) {
        if (Cloud.INSTANCE.modManager.isModToggled("Hit Color")) {
            instance.put(dev.cloudmc.feature.mod.impl.HitColorMod.getAlpha());
        } else {
            instance.put(0.3f);
        }
        return instance;
    }
}