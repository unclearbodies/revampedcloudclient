package first.rain.anticheat.util.anticheat.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import first.rain.anticheat.config.cfg;
import first.rain.anticheat.util.anticheat.AlertManager;
import first.rain.anticheat.util.anticheat.PlayerEligibility;

public class LegitScaffoldCheck {
   private static final Map<UUID, Long> lastCrouchStart = new HashMap<UUID, Long>();
   private static final Map<UUID, Long> lastCrouchEnd = new HashMap<UUID, Long>();
   private static final Map<UUID, Boolean> wasSneaking = new HashMap<UUID, Boolean>();
   private static final Map<UUID, Long> lastSwingTick = new HashMap<UUID, Long>();
   private static final Map<UUID, List<Integer>> crouchDurations = new HashMap<UUID, List<Integer>>();
   private static final Map<UUID, Long> lastFlagTick = new HashMap<UUID, Long>();
   private static final Map<UUID, Integer> violationLevels = new HashMap<UUID, Integer>();
   private final long cooldownTicks = 60L;
   private boolean flagged = false;

   public void anticheatCheck(EntityPlayer player) {
      if (!cfg.v.detectLegitScaffold) {
         return;
      }
      if (!PlayerEligibility.shouldCheckPlayer(player)) {
         this.forgetPlayer(player == null ? null : player.getUniqueID());
         return;
      }

      UUID uuid = player.getUniqueID();
      long tick = (long)player.ticksExisted;
      boolean currSneak = player.isSneaking();
      boolean prevSneak = wasSneaking.getOrDefault(uuid, false);
      if (currSneak && !prevSneak) {
         lastCrouchStart.put(uuid, tick);
      } else if (!currSneak && prevSneak) {
         lastCrouchEnd.put(uuid, tick);
         long start = lastCrouchStart.getOrDefault(uuid, tick - 1L);
         int duration = (int)(tick - start);
         List<Integer> durations = crouchDurations.computeIfAbsent(uuid, (k) -> new ArrayList<Integer>());
         durations.add(0, duration);
         if (durations.size() > 5) {
            durations.remove(5);
         }
      }

      wasSneaking.put(uuid, currSneak);
      if (player.isSwingInProgress && player.hurtTime != player.maxHurtTime) {
         lastSwingTick.put(uuid, tick);
      }

      if (player.rotationPitch >= 60.0F && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock && player.onGround) {
         long end = lastCrouchEnd.getOrDefault(uuid, 0L);
         long swing = lastSwingTick.getOrDefault(uuid, Long.MIN_VALUE);
         int crouchDuration = (int)(end - lastCrouchStart.getOrDefault(uuid, end - 1L));
         boolean quickCrouch = crouchDuration >= 1 && crouchDuration <= 2;
         boolean swingTiming = swing >= end && swing <= end + 1L;
         List<Integer> durations = crouchDurations.getOrDefault(uuid, Collections.<Integer>emptyList());
         boolean consistent = durations.size() >= 3 && durations.get(0) <= 2 && durations.get(1) <= 2 && durations.get(2) <= 2;
         if (quickCrouch && swingTiming && consistent) {
            long lastFlag = lastFlagTick.getOrDefault(uuid, 0L);
            if (tick - lastFlag >= this.cooldownTicks) {
               this.flagged = true;
               lastFlagTick.put(uuid, tick);
               int vl = violationLevels.getOrDefault(uuid, 0) + 1;
               violationLevels.put(uuid, vl);
               AlertManager.flag(player, AlertManager.CheckType.LEGIT_SCAFFOLD, vl);
            } else {
               this.flagged = false;
            }
         } else {
            this.flagged = false;
         }
      } else {
         this.flagged = false;
      }
   }

   public boolean failedLegitScaffold() {
      return this.flagged;
   }

   public void reset() {
      this.flagged = false;
   }

   public void forgetPlayer(UUID uuid) {
      if (uuid == null) {
         return;
      }
      lastCrouchStart.remove(uuid);
      lastCrouchEnd.remove(uuid);
      wasSneaking.remove(uuid);
      lastSwingTick.remove(uuid);
      crouchDurations.remove(uuid);
      lastFlagTick.remove(uuid);
      violationLevels.remove(uuid);
      if (violationLevels.isEmpty()) {
         this.flagged = false;
      }
   }

   public void retainPlayers(Set<UUID> playerIds) {
      lastCrouchStart.keySet().retainAll(playerIds);
      lastCrouchEnd.keySet().retainAll(playerIds);
      wasSneaking.keySet().retainAll(playerIds);
      lastSwingTick.keySet().retainAll(playerIds);
      crouchDurations.keySet().retainAll(playerIds);
      lastFlagTick.keySet().retainAll(playerIds);
      violationLevels.keySet().retainAll(playerIds);
      if (violationLevels.isEmpty()) {
         this.flagged = false;
      }
   }

   public static void clear() {
      lastCrouchStart.clear();
      lastCrouchEnd.clear();
      wasSneaking.clear();
      lastSwingTick.clear();
      crouchDurations.clear();
      lastFlagTick.clear();
      violationLevels.clear();
   }
}
