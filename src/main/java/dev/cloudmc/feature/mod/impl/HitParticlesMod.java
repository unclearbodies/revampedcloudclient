package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HitParticlesMod extends Mod {
    
    private Setting multiplierSetting;

    public HitParticlesMod() {
        super("Hit Particles", "Spawns additional particles on hit.", Type.Visual);
        
        multiplierSetting = new Setting("Multiplier", this, 1, 10, 3);
        Cloud.INSTANCE.settingManager.addSetting(multiplierSetting);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.entityPlayer == mc.thePlayer && event.target != null) {
            int amount = (int) multiplierSetting.getCurrentNumber();
            for (int i = 0; i < amount; i++) {
                mc.effectRenderer.emitParticleAtEntity(event.target, EnumParticleTypes.CRIT_MAGIC);
                mc.effectRenderer.emitParticleAtEntity(event.target, EnumParticleTypes.CRIT);
            }
        }
    }
}
