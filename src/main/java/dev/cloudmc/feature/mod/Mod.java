/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod;

import net.minecraftforge.common.MinecraftForge;

public class Mod {

    private String name;
    private String description;
    private Type type;
    private boolean toggled;

    public Mod(String name, String description, Type type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /**
     * Called after this mod is enabled. Override to add mod-specific enable logic.
     * <p>
     * Forge event bus registration is already handled by {@code callMethod()} before this
     * is invoked — you do NOT need to call {@code super.onEnable()} from subclasses.
     * This method is purely a hook for mod-specific setup (e.g., capturing current gamma).
     */
    public void onEnable() {
    }

    /**
     * Called after this mod is disabled. Override to add mod-specific disable logic.
     * <p>
     * Forge event bus unregistration is already handled by {@code callMethod()} before this
     * is invoked — you do NOT need to call {@code super.onDisable()} from subclasses.
     * This method is purely a hook for mod-specific teardown (e.g., restoring gamma).
     */
    public void onDisable() {
    }

    /**
     * Whether this mod should have a "Toggle Keybind" setting auto-registered.
     * Override and return {@code false} for mods that shouldn't be toggled via keybind
     * (e.g., Cape, which is always-on when enabled and has no meaningful toggle key).
     *
     * @return true if ModManager should auto-register a keybind setting for this mod
     */
    public boolean hasKeybind() {
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isToggled() {
        return toggled;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setToggled(boolean toggled) {
        if (this.toggled == toggled) return;
        this.toggled = toggled;
        callMethod();
    }

    public void toggle() {
        toggled = !toggled;
        callMethod();
    }

    private void callMethod() {
        if (toggled) {
            MinecraftForge.EVENT_BUS.register(this);
            if (dev.cloudmc.Cloud.INSTANCE != null && dev.cloudmc.Cloud.INSTANCE.hudEditor != null) {
                dev.cloudmc.gui.hudeditor.impl.HudMod hudMod = dev.cloudmc.Cloud.INSTANCE.hudEditor.getHudMod(this.name);
                if (hudMod != null) {
                    MinecraftForge.EVENT_BUS.register(hudMod);
                }
            }
            onEnable();
        }
        else {
            MinecraftForge.EVENT_BUS.unregister(this);
            if (dev.cloudmc.Cloud.INSTANCE != null && dev.cloudmc.Cloud.INSTANCE.hudEditor != null) {
                dev.cloudmc.gui.hudeditor.impl.HudMod hudMod = dev.cloudmc.Cloud.INSTANCE.hudEditor.getHudMod(this.name);
                if (hudMod != null) {
                    MinecraftForge.EVENT_BUS.unregister(hudMod);
                }
            }
            onDisable();
        }
    }
}
