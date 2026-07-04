package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Pattern;

public class SessionStatsMod extends Mod {
    
    public enum GameMode {
        BEDWARS("BedWars", "bed\\s*wars"),
        SKYWARS("SkyWars", "sky\\s*wars"),
        DUELS_BRIDGE("Bridge", "bridge\\s*duel|the\\s*bridge"),
        DUELS_COMBO("Combo", "combo\\s*duel"),
        DUELS_POT("Pot PvP", "uhc|pot\\s*pvp|nodebuff"),
        DUELS_SUMO("Sumo", "sumo"),
        DUELS_GENERIC("Duels", "duel"),
        SKYBLOCK("SkyBlock", "sky\\s*block"),
        UNKNOWN(null, null);

        public final String display;
        public final Pattern pattern;

        GameMode(String display, String regex) {
            this.display = display;
            this.pattern = regex != null ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : null;
        }

        public static GameMode fromTitle(String rawTitle) {
            if (rawTitle == null || rawTitle.isEmpty()) return UNKNOWN;
            for (GameMode mode : values()) {
                if (mode.pattern != null && mode.pattern.matcher(rawTitle).find()) {
                    return mode;
                }
            }
            return UNKNOWN;
        }
    }

    public static int kills = 0;
    public static int deaths = 0;
    public static int wins = 0;
    
    private static GameMode currentMode = GameMode.UNKNOWN;
    private static String rawScoreboardTitle = "";
    
    private int tickCounter = 0;

    public SessionStatsMod() {
        super("Session Stats", "Tracks your session K/D and Wins.", Type.Hud);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        String name = Minecraft.getMinecraft().getSession().getUsername();
        
        // Track Wins (reuses same patterns as AutoGGMod)
        if (message.startsWith("1st Killer - ") || 
            message.startsWith("1st Place - ") || 
            message.startsWith("Winner: ") || 
            message.startsWith("Winning Team: ") ||
            message.matches("(?i)^(?>1st|2nd|3rd) Place:.*")) {
            wins++;
        }
        
        // Track Kills / Deaths using regex to avoid substring false positives
        if (message.matches(".* (was killed by|thrown into the void by|slain by|shot by) " + name + "\\.?.*")) {
            kills++;
        } else if (message.matches(name + " (was killed by|thrown into the void by|slain by|shot by|died|fell).*")) {
            deaths++;
        } else if (message.equals("You died!")) {
            deaths++;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        tickCounter++;
        if (tickCounter >= 20) { // Update every 1 second
            tickCounter = 0;
            
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && mc.theWorld.getScoreboard() != null) {
                Scoreboard scoreboard = mc.theWorld.getScoreboard();
                ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
                if (objective != null) {
                    String newTitle = StringUtils.stripControlCodes(objective.getDisplayName());
                    GameMode newMode = GameMode.fromTitle(newTitle);
                    
                    if (newMode != currentMode) {
                        kills = 0;
                        deaths = 0;
                        wins = 0;
                        currentMode = newMode;
                    }
                    
                    rawScoreboardTitle = newTitle;
                } else {
                    rawScoreboardTitle = "";
                    currentMode = GameMode.UNKNOWN;
                }
            } else {
                rawScoreboardTitle = "";
                currentMode = GameMode.UNKNOWN;
            }
        }
    }
    
    public static GameMode getCurrentMode() {
        return currentMode;
    }
}
