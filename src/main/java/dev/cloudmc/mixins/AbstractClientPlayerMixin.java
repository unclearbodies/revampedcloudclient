package dev.cloudmc.mixins;

import dev.cloudmc.Cloud;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if (Cloud.INSTANCE.modManager.getMod("Cape") != null && Cloud.INSTANCE.modManager.getMod("Cape").isToggled()) {
            cir.setReturnValue(new ResourceLocation("cloudmc/cape.png"));
        }
    }
}
