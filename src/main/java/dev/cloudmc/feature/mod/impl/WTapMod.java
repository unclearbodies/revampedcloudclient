package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WTapMod extends Mod {
    public WTapMod() {
        super("W-Tap", "Automatically resets your sprint to deal more knockback.", Type.Mechanic);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (event.entityPlayer == Minecraft.getMinecraft().thePlayer) {
            if (Minecraft.getMinecraft().thePlayer.isSprinting()) {
                Minecraft.getMinecraft().thePlayer.setSprinting(false);
            }
        }
    }
}
