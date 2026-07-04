/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod;

import dev.cloudmc.feature.mod.impl.*;
import dev.cloudmc.feature.mod.impl.crosshair.CrosshairMod;

import java.util.ArrayList;

public class ModManager {

    public java.util.LinkedHashMap<String, Mod> mods = new java.util.LinkedHashMap<>();
    private java.util.HashMap<String, Mod> lookupMap = new java.util.HashMap<>();
    private ArrayList<Mod> cachedMods = null;

    public ModManager() {
        init();
    }

    /**
     * Initializes all mods
     */

    public void init() {
        addMod(new AutoClickerMod());
        addMod(new ClutchMod());
        addMod(new ToggleSprintMod());
        addMod(new ToggleSneakMod());
        addMod(new FpsMod());
        addMod(new KeystrokesMod());
        addMod(new ArmorMod());
        addMod(new FullbrightMod());
        addMod(new SnaplookMod());
        addMod(new CoordinatesMod());
        addMod(new ServerAddressMod());
        addMod(new PingMod());
        addMod(new CpsMod());
        addMod(new PotionMod());
        addMod(new TimeMod());
        addMod(new SpeedIndicatorMod());
        addMod(new AnimationMod());
        addMod(new FreelookMod());
        addMod(new CrosshairMod());
        addMod(new MotionblurMod());
        addMod(new GuiTweaksMod());
        addMod(new BlockOverlayMod());
        addMod(new BlockInfoMod());
        addMod(new ReachDisplayMod());
        addMod(new ZoomMod());
        addMod(new DayCounterMod());
        addMod(new NoHurtCamMod());
        addMod(new ScrollTooltipsMod());
        addMod(new ParticleMultiplierMod());
        addMod(new NickHiderMod());
        addMod(new ScoreboardMod());
        addMod(new BossbarMod());
        addMod(new DirectionMod());
        addMod(new HitColorMod());
        addMod(new TimeChangerMod());
        addMod(new NameTagMod());
        addMod(new WTapMod());
        addMod(new ComboMod());
        addMod(new AutoSprintMod());
        addMod(new AntiBlindnessMod());
        addMod(new AutoGGMod());
        addMod(new SessionStatsMod());
        addMod(new SaturationMod());
        addMod(new HitDistanceMod());
        addMod(new CustomSkyMod());
        addMod(new HitParticlesMod());
        addMod(new TPSMod());
        addMod(new CapeMod());
        addMod(new HitboxOnlyMod());
        addMod(new DiscordRPCMod());
        addMod(new BorderlessFullscreenMod());
    }

    /**
     * @return Returns an Arraylist of all mods
     */

    public ArrayList<Mod> getMods() {
        if (cachedMods == null) {
            cachedMods = new ArrayList<>(mods.values());
        }
        return cachedMods;
    }

    /**
     * Returns a given mod using its name
     * @param name The name of the mod
     * @return The returned mod
     */

    public Mod getMod(String name) {
        Mod mod = lookupMap.get(name.toLowerCase());
        if (mod != null) {
            return mod;
        }
        throw new IllegalArgumentException("Mod not found: " + name);
    }

    /**
     * Defensive helper to check if a mod is toggled
     * @param name The name of the mod
     * @return true if toggled, false if not toggled or not found
     */
    public boolean isModToggled(String name) {
        Mod mod = lookupMap.get(name.toLowerCase());
        return mod != null && mod.isToggled();
    }

    /**
     * Adds a mod to the list
     * @param mod The mod which should be added
     */

    public void addMod(Mod mod) {
        if (mod.getType() != dev.cloudmc.feature.mod.Type.Hud && mod.hasKeybind()) {
            dev.cloudmc.Cloud.INSTANCE.settingManager.addSetting(new dev.cloudmc.feature.setting.Setting("Toggle Keybind", mod, org.lwjgl.input.Keyboard.KEY_NONE));
        }
        mods.put(mod.getName(), mod);
        lookupMap.put(mod.getName().toLowerCase(), mod);
        cachedMods = null;
    }

    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
    public void onKey(net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent event) {
        if (org.lwjgl.input.Keyboard.getEventKeyState()) {
            int key = org.lwjgl.input.Keyboard.getEventKey();
            if (key != org.lwjgl.input.Keyboard.KEY_NONE) {
                for (Mod mod : getMods()) {
                    try {
                        dev.cloudmc.feature.setting.Setting setting = dev.cloudmc.Cloud.INSTANCE.settingManager.getSettingByModAndName(mod.getName(), "Toggle Keybind");
                        if (setting.getKey() == key) {
                            mod.toggle();
                        }
                    } catch (IllegalArgumentException e) {
                        // Mod doesn't have a Toggle Keybind setting (e.g., HUD mods, Cape)
                    }
                }
            }
        }
    }
}
