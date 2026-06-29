package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGGMod extends Mod {
    public AutoGGMod() {
        super("Auto GG", "Automatically says GG at the end of a game.", Type.Mechanic);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String stripped = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (stripped.startsWith("1st Killer - ") || 
            stripped.startsWith("1st Place - ") || 
            stripped.startsWith("Winner: ") || 
            stripped.startsWith("Winning Team: ") ||
            stripped.matches("(?i)^(?>1st|2nd|3rd) Place:.*")) {
            
            Minecraft.getMinecraft().thePlayer.sendChatMessage("gg");
        }
    }
}
