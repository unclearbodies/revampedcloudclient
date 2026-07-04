/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.helpers.DiscordHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DiscordRPCMod extends Mod {

    private int tickCounter = 0;

    public DiscordRPCMod() {
        super(
                "Discord RPC",
                "Shows your current game activity on Discord.",
                Type.Tweaks
        );
    }

    @Override
    public void onEnable() {
        DiscordHelper.start();
    }

    @Override
    public void onDisable() {
        DiscordHelper.stop();
    }

    /**
     * Updates the Discord presence every ~5 seconds (100 ticks)
     * to reflect the current game state without spamming the API.
     */
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter >= 100) {
            tickCounter = 0;
            DiscordHelper.update();
        }
    }
}
