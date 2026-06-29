package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;

public class SessionStatsMod extends Mod {
    
    public static int kills = 0;
    public static int deaths = 0;
    public static int wins = 0;
    
    public SessionStatsMod() {
        super("Session Stats", "Tracks your session K/D and Wins.", Type.HUD);
    }
}
