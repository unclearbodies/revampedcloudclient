package dev.cloudmc.mixins;

import dev.cloudmc.Cloud;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class WorldMixin {

    @Redirect(method = "getCelestialAngle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;getWorldTime()J"))
    public long setCelestialAngle(WorldInfo instance) {
        if (Cloud.INSTANCE.modManager.isModToggled("TimeChanger")) {
            return (long) (instance.getWorldTime() *
                    dev.cloudmc.feature.mod.impl.TimeChangerMod.getSpeed() +
                    dev.cloudmc.feature.mod.impl.TimeChangerMod.getOffset());
        }
        return instance.getWorldTime();
    }
}
