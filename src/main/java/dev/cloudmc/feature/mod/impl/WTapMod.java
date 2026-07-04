package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class WTapMod extends Mod {

    private int wTapTick = 0;
    private int actionDelay = 1;
    private int state = 0;

    private Setting chanceSetting;
    private Setting rangeSetting;
    private Setting actionTicksSetting;

    public WTapMod() {
        super("W-Tap", "Simulates actual W keypresses to reset sprint dynamically.", Type.Mechanic);
        
        chanceSetting = new Setting("Chance", this, 0.0f, 100.0f, 100.0f);
        rangeSetting = new Setting("Range", this, 1.0f, 6.0f, 3.5f);
        actionTicksSetting = new Setting("Action Ticks", this, 1.0f, 10.0f, 1.0f);

        Cloud.INSTANCE.settingManager.addSetting(chanceSetting);
        Cloud.INSTANCE.settingManager.addSetting(rangeSetting);
        Cloud.INSTANCE.settingManager.addSetting(actionTicksSetting);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.entityPlayer != mc.thePlayer) return;
        
        if (mc.currentScreen != null) return;
        if (!mc.thePlayer.isEntityAlive()) return;
        
        if (event.target == null || !(event.target instanceof EntityPlayer)) return;
        
        float range = rangeSetting.getCurrentNumber();
        if (mc.thePlayer.getDistanceToEntity(event.target) > range) return;
        
        int forwardKey = mc.gameSettings.keyBindForward.getKeyCode();
        if (!Keyboard.isKeyDown(forwardKey)) return;
        
        // Only trigger WTap if we are actually sprinting, otherwise there's no knockback benefit to reset
        if (!mc.thePlayer.isSprinting()) return;
        
        float chance = chanceSetting.getCurrentNumber();
        if (Math.random() * 100f > chance) return;
        
        if (state == 0) {
            state = 1;
            wTapTick = (int) actionTicksSetting.getCurrentNumber();
            
            KeyBinding.setKeyBindState(forwardKey, false);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (state == 0) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            state = 0;
            return;
        }
        
        wTapTick--;
        if (wTapTick <= 0) {
            int forwardKey = mc.gameSettings.keyBindForward.getKeyCode();
            state = 0;
            KeyBinding.setKeyBindState(forwardKey, Keyboard.isKeyDown(forwardKey));
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && state != 0) {
            int forwardKey = mc.gameSettings.keyBindForward.getKeyCode();
            KeyBinding.setKeyBindState(forwardKey, Keyboard.isKeyDown(forwardKey));
        }
        state = 0;
    }
    
    public boolean isActive() {
        return state != 0;
    }
}
