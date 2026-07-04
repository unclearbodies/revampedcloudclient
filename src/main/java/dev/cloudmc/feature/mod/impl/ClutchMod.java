package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Auto-clutch: searches for and places a block beneath the player the instant
 * they start falling, rotating and placing in the same tick to keep maximum
 * margin before they hit the void.
 */
public class ClutchMod extends Mod {

    private static final int HOTBAR_SLOTS = 9;
    private int clutchCooldown = 0;

    private Setting searchDepthSetting;
    private Setting jitterSetting;
    private Setting predictMomentumSetting;

    public ClutchMod() {
        super("Clutch", "Actively clutches and saves a fall using silent rotations", Type.Mechanic);

        searchDepthSetting = new Setting("Search Depth", this, 1.0f, 6.0f, 4.0f);
        jitterSetting = new Setting("Jitter", this, 0.0f, 10.0f, 2.0f);
        predictMomentumSetting = new Setting("Predict Momentum", this, true);

        Cloud.INSTANCE.settingManager.addSetting(searchDepthSetting);
        Cloud.INSTANCE.settingManager.addSetting(jitterSetting);
        Cloud.INSTANCE.settingManager.addSetting(predictMomentumSetting);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        if (clutchCooldown > 0) {
            clutchCooldown--;
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            disable();
            return;
        }

        if (!isToggled()) return;

        EntityPlayerSP player = mc.thePlayer;
        if (player.onGround) return;

        int slot = findPlaceableHotbarSlot();
        if (slot == -1) return;

        attemptPlacement(mc, player, slot);
    }

    private void attemptPlacement(Minecraft mc, EntityPlayerSP player, int slot) {
        int searchDepth = (int) searchDepthSetting.getCurrentNumber();
        boolean predict = predictMomentumSetting.isCheckToggled();

        double targetX = player.posX;
        double targetY = player.posY;
        double targetZ = player.posZ;
        if (predict) {
            targetX += player.motionX * 2.5;
            targetZ += player.motionZ * 2.5;
            targetY += player.motionY * 2.0;
        }
        BlockPos playerPos = new BlockPos(targetX, targetY, targetZ);

        for (int d = 1; d <= searchDepth; d++) {
            BlockPos targetEmpty = playerPos.down(d);
            if (isSolid(mc.theWorld, targetEmpty)) continue;

            BlockPos against = null;
            EnumFacing face = null;

            if (isSolid(mc.theWorld, targetEmpty.down())) {
                against = targetEmpty.down();
                face = EnumFacing.UP;
            } else {
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    BlockPos adjacent = targetEmpty.offset(facing);
                    if (isSolid(mc.theWorld, adjacent)) {
                        against = adjacent;
                        face = facing.getOpposite();
                        break;
                    }
                }
            }

            if (against == null) continue;

            int previousSlot = player.inventory.currentItem;
            player.inventory.currentItem = slot;
            ItemStack stack = player.inventory.getStackInSlot(slot);

            Vec3 hitVec = hitVecFor(against, face);
            float[] rotations = getRotations(player, hitVec);

            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], player.onGround));

            boolean placed = mc.playerController.onPlayerRightClick(player, mc.theWorld, stack, against, face, hitVec);
            if (placed) {
                player.swingItem();
            }

            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(player.rotationYaw, player.rotationPitch, player.onGround));

            player.inventory.currentItem = previousSlot;

            if (!placed) {
                clutchCooldown = 3;
            }
            return;
        }
    }

    private Vec3 hitVecFor(BlockPos against, EnumFacing face) {
        return new Vec3(
                against.getX() + 0.5 + face.getFrontOffsetX() * 0.5,
                against.getY() + 0.5 + face.getFrontOffsetY() * 0.5,
                against.getZ() + 0.5 + face.getFrontOffsetZ() * 0.5
        );
    }

    private float[] getRotations(EntityPlayerSP player, Vec3 hitVec) {
        double diffX = hitVec.xCoord - player.posX;
        double diffY = hitVec.yCoord - (player.posY + player.getEyeHeight());
        double diffZ = hitVec.zCoord - player.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        float jitter = jitterSetting.getCurrentNumber();
        if (jitter > 0) {
            yaw += (float) ((Math.random() - 0.5) * 2.0 * jitter);
            pitch += (float) ((Math.random() - 0.5) * 2.0 * jitter);
        }

        return new float[]{yaw, pitch};
    }

    private int findPlaceableHotbarSlot() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        for (int slot = 0; slot < HOTBAR_SLOTS; slot++) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack == null) continue;
            Item item = stack.getItem();
            if (!(item instanceof ItemBlock)) continue;
            String itemName = item.getUnlocalizedName();
            if (itemName == null) continue;
            if (itemName.contains("lilypad") || itemName.contains("stained")) continue;
            return slot;
        }
        return -1;
    }

    private boolean isSolid(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block.getMaterial() != Material.air;
    }

    private void disable() {
        Cloud.INSTANCE.modManager.getMod(getName()).setToggled(false);
    }
}