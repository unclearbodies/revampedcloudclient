package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class TPSMod extends Mod {
    
    private static long lastTimeUpdate = -1;
    private static float currentTps = 20.0f;

    public TPSMod() {
        super("TPS", "Displays the server's TPS (Ticks Per Second).", Type.Hud);
    }

    public static void onTimeUpdate() {
        if (lastTimeUpdate != -1) {
            long timeDiff = System.currentTimeMillis() - lastTimeUpdate;
            if (timeDiff > 0) {
                // S03PacketTimeUpdate is sent every 20 ticks
                currentTps = Math.min(20.0f, (20.0f / (timeDiff / 1000.0f)));
            }
        }
        lastTimeUpdate = System.currentTimeMillis();
    }
    
    @SubscribeEvent
    public void onConnect(ClientConnectedToServerEvent event) {
        lastTimeUpdate = -1;
        currentTps = 20.0f;
    }

    public static float getTPS() {
        return currentTps;
    }
}
