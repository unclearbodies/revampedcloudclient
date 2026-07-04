/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.helpers;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class CpsHelper {

    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

    @SubscribeEvent
    public void onClick(MouseEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null) return; // don't register cps in GUIs
        long time = System.currentTimeMillis();

        if (!event.buttonstate) return;

        if (event.button == 0) leftClicks.add(time);
        else if (event.button == 1) rightClicks.add(time);

        removeOldClicks(time);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        removeOldClicks(System.currentTimeMillis());
    }

    public int getCPS(int mouseButton) {
        return mouseButton == 0 ? leftClicks.size() : rightClicks.size();
    }

    public void removeOldClicks(long currentTime) {
        leftClicks.removeIf(e -> e + 1000 < currentTime);
        rightClicks.removeIf(e -> e + 1000 < currentTime);
    }

    public void addLeftClick() {
        long time = System.currentTimeMillis();
        leftClicks.add(time);
        removeOldClicks(time);
    }

    public void addRightClick() {
        long time = System.currentTimeMillis();
        rightClicks.add(time);
        removeOldClicks(time);
    }
}
