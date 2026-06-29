package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HitDistanceMod extends Mod {
    
    private static double distance = 0.0;

    public HitDistanceMod() {
        super("Hit Distance", "Displays the distance of your last hit.", Type.HUD);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.entityPlayer == mc.thePlayer && event.target != null) {
            distance = mc.thePlayer.getDistanceToEntity(event.target);
        }
    }

    public static double getDistance() {
        return distance;
    }
}
