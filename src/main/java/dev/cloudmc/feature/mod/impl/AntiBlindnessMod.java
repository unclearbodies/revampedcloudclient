package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiBlindnessMod extends Mod {
    public AntiBlindnessMod() {
        super("Anti Blindness", "Removes the visual blindness and nausea effects.", Type.Visual);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            if (mc.thePlayer.isPotionActive(Potion.blindness)) {
                mc.thePlayer.removePotionEffect(Potion.blindness.id);
            }
            if (mc.thePlayer.isPotionActive(Potion.confusion)) {
                mc.thePlayer.removePotionEffect(Potion.confusion.id);
            }
        }
    }
}
