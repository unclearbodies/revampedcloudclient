package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ComboMod extends Mod {
    
    private static int combo;
    private static long lastHitTime;
    private boolean wasHurt;

    public ComboMod() {
        super("Combo Counter", "Displays your current hit combo.", Type.Hud);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (System.currentTimeMillis() - lastHitTime > 2000) {
            combo = 0;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            if (mc.thePlayer.hurtTime > 0) {
                if (!wasHurt) {
                    combo = 0; // Reset combo when damaged
                    wasHurt = true;
                }
            } else {
                wasHurt = false;
            }
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.entityPlayer == mc.thePlayer && event.target != null && !event.target.isEntityInvulnerable(net.minecraft.util.DamageSource.generic)) {
            combo++;
            lastHitTime = System.currentTimeMillis();
        }
    }

    public static int getCombo() {
        return combo;
    }
}
