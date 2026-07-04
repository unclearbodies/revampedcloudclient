package first.rain.anticheat.util.anticheat;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldSettings.GameType;

/**
 * Shared player gate for anticheat checks. Servers can spawn NPCs/bots as
 * EntityPlayer instances, so Java type alone is not proof that the entity is a
 * real connected player.
 */
public final class PlayerEligibility {
   private PlayerEligibility() {
   }

   /** True for real players, including the local client player. */
   public static boolean isRealPlayer(EntityPlayer player) {
      Minecraft mc = Minecraft.getMinecraft();
      if (player == null || mc == null || mc.theWorld == null || player.isDead) {
         return false;
      }
      if (player.getHealth() <= 0.0F || player.isSpectator()) {
         return false;
      }

      NetHandlerPlayClient netHandler = mc.getNetHandler();
      if (netHandler == null) {
         return false;
      }

      UUID uuid = player.getUniqueID();
      if (uuid == null) {
         return false;
      }

      NetworkPlayerInfo info = netHandler.getPlayerInfo(uuid);
      if (info == null || info.getGameType() == GameType.SPECTATOR) {
         return false;
      }

      String name = player.getName();
      if (name == null || name.isEmpty() || name.length() > 16 || !name.matches("^[a-zA-Z0-9_]+$")) {
         return false;
      }
      return netHandler.getPlayerInfo(name) == info;
   }

   /** True for remote players that should be analyzed as suspects. */
   public static boolean shouldCheckPlayer(EntityPlayer player) {
      Minecraft mc = Minecraft.getMinecraft();
      return isRealPlayer(player) && mc != null && mc.thePlayer != null && player != mc.thePlayer;
   }

   /** True for real players that can be used as killaura geometry targets. */
   public static boolean shouldUseAsTarget(EntityPlayer candidate, EntityPlayer attacker) {
      return candidate != attacker && isRealPlayer(candidate);
   }
}
