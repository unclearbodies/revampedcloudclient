package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoSprintMod extends Mod {
    public AutoSprintMod() {
        super("Auto Sprint", "Always sprints without needing a keybind.", Type.Mechanic);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.gameSettings.keyBindForward.isKeyDown()) {
            if (!mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}
