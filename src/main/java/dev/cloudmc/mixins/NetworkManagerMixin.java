package dev.cloudmc.mixins;

import dev.cloudmc.feature.mod.impl.TPSMod;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class NetworkManagerMixin {
    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void onReceivePacket(io.netty.channel.ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_, CallbackInfo ci) {
        if (p_channelRead0_2_ instanceof S03PacketTimeUpdate) {
            TPSMod.onTimeUpdate();
        }
    }
}
