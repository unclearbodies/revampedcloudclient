package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoSprintMod extends Mod {

    private WTapMod cachedWTapMod;
    private boolean wTapLookedUp = false;

    public AutoSprintMod() {
        super("Auto Sprint", "Always sprints without needing a keybind.", Type.Mechanic);
    }

    private WTapMod getWTapMod() {
        if (!wTapLookedUp) {
            cachedWTapMod = (WTapMod) Cloud.INSTANCE.modManager.getMod("W-Tap");
            wTapLookedUp = true;
        }
        return cachedWTapMod;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        WTapMod wTapMod = getWTapMod();
        if (wTapMod != null && wTapMod.isToggled() && wTapMod.isActive()) {
            return;
        }
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            if (!mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}
