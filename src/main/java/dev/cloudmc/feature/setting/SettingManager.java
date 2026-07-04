/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.setting;

import dev.cloudmc.feature.mod.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingManager {

    public ArrayList<Setting> settingList;
    private Map<String, Map<String, Setting>> settingCache;

    public SettingManager() {
        settingList = new ArrayList<>();
        settingCache = new HashMap<>();
    }

    /**
     * Adds a setting to the settings List
     * @param setting The setting to be added
     */

    public void addSetting(Setting setting) {
        settingList.add(setting);
        settingCache.computeIfAbsent(setting.getMod().getName().toLowerCase(), k -> new HashMap<>())
                    .put(setting.getName().toLowerCase(), setting);
    }

    /**
     * @return Returns an Arraylist of all settings
     */

    public ArrayList<Setting> getSettingList() {
        return settingList;
    }

    /**
     * Returns a list of all settings from a given mod
     * @param mod The mod
     * @return The Arraylist of settings
     */

    public ArrayList<Setting> getSettingsByMod(Mod mod) {
        ArrayList<Setting> result = new ArrayList<>();
        Map<String, Setting> modSettings = settingCache.get(mod.getName().toLowerCase());
        if (modSettings != null) {
            result.addAll(modSettings.values());
        }
        return result;
    }

    /**
     * Returns a setting with a given mod name and setting name.
     * Throws IllegalArgumentException if the setting is not found,
     * matching the behavior of ModManager.getMod() and OptionManager.getOptionByName().
     *
     * @param modName The mod name
     * @param setName The setting name
     * @return The setting
     * @throws IllegalArgumentException if no setting matches the given mod and setting name
     */

    public Setting getSettingByModAndName(String modName, String setName) {
        Map<String, Setting> modSettings = settingCache.get(modName.toLowerCase());
        if (modSettings != null) {
            Setting setting = modSettings.get(setName.toLowerCase());
            if (setting != null) {
                return setting;
            }
        }
        throw new IllegalArgumentException("Setting not found: " + modName + " / " + setName);
    }
}
