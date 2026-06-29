/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc;

import dev.cloudmc.config.ConfigLoader;
import dev.cloudmc.config.ConfigSaver;
import dev.cloudmc.feature.mod.ModManager;
import dev.cloudmc.feature.option.OptionManager;
import dev.cloudmc.feature.setting.SettingManager;
import dev.cloudmc.gui.hudeditor.HudEditor;
import dev.cloudmc.helpers.CpsHelper;
import dev.cloudmc.helpers.MessageHelper;
import dev.cloudmc.helpers.font.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

import java.io.FileNotFoundException;
import java.io.IOException;

@Mod(
        modid = Cloud.modID,
        name = Cloud.modName,
        version = Cloud.modVersion,
        acceptedMinecraftVersions = "[1.8.9]"
)
public class Cloud {

    @Mod.Instance()
    public static Cloud INSTANCE;

    public static final String modID = "cloudmc";
    public static final String modName = "Cloud";
    public static final String modVersion = "1.4.1 [1.8.9]";

    public Minecraft mc = Minecraft.getMinecraft();

    public ModManager modManager;
    public SettingManager settingManager;
    public HudEditor hudEditor;
    public OptionManager optionManager;
    public FontHelper fontHelper;
    public CpsHelper cpsHelper;
    public MessageHelper messageHelper;
    public first.rain.anticheat.Rain rainAnticheat;

    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Cloud.modID);

    /**
     * Initializes the client
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle(Cloud.modName + " Client " + Cloud.modVersion);
        registerEvents(
                cpsHelper = new CpsHelper(),
                settingManager = new SettingManager(),
                modManager = new ModManager(),
                optionManager = new OptionManager(),
                hudEditor = new HudEditor(),
                fontHelper = new FontHelper(),
                messageHelper = new MessageHelper()
        );

        rainAnticheat = new first.rain.anticheat.Rain();
        rainAnticheat.init(event);

        try {
            if (!ConfigSaver.configExists()) {
                ConfigSaver.saveConfig();
            }
            ConfigLoader.loadConfig();
        } catch (Exception e) {
            LOGGER.error("Failed to load/save config", e);
        }
        fontHelper.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ConfigSaver.saveConfig();
            } catch (Exception e) {
                LOGGER.error("Failed to save config on shutdown", e);
            }
        }));
    }

    private void registerEvents(Object... events) {
        for (Object event : events) {
            MinecraftForge.EVENT_BUS.register(event);
        }
    }
}