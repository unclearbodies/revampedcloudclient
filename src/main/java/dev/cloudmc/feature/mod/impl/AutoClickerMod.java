package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class AutoClickerMod extends Mod {

    // CPS bounds/defaults as named constants instead of repeated magic numbers
    private static final float MIN_CPS = 1.0f;
    private static final float MAX_CPS = 20.0f;
    private static final float DEFAULT_CPS = 13.0f;

    // Cached setting references, resolved once instead of via string lookup every tick
    private Setting cpsSetting;
    private Setting rightCpsSetting;
    private Setting inventoryFillSetting;
    private Setting blockhitSetting;
    private Setting rightClickModeSetting;
    private Setting breakBlocksSetting;

    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;

    // Edge-detection state: true while the button is being held across ticks
    private boolean wasLeftDown = false;
    private boolean wasRightDown = false;

    public AutoClickerMod() {
        super("AutoClicker", "Hold attack button to automatically click", Type.Mechanic);

        cpsSetting = new Setting("CPS", this, MIN_CPS, MAX_CPS, DEFAULT_CPS);
        rightCpsSetting = new Setting("Right CPS", this, MIN_CPS, MAX_CPS, DEFAULT_CPS);
        inventoryFillSetting = new Setting("Inventory Fill", this, false);
        blockhitSetting = new Setting("Blockhit", this, false);
        rightClickModeSetting = new Setting("Right Click Mode", this, false);
        breakBlocksSetting = new Setting("Break Blocks", this, false);

        Cloud.INSTANCE.settingManager.addSetting(cpsSetting);
        Cloud.INSTANCE.settingManager.addSetting(rightCpsSetting);
        Cloud.INSTANCE.settingManager.addSetting(inventoryFillSetting);
        Cloud.INSTANCE.settingManager.addSetting(blockhitSetting);
        Cloud.INSTANCE.settingManager.addSetting(rightClickModeSetting);
        Cloud.INSTANCE.settingManager.addSetting(breakBlocksSetting);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        boolean inventoryFill = inventoryFillSetting.isCheckToggled();
        if (mc.currentScreen != null && !inventoryFill) {
            // Make sure nothing is left "held" while a screen is open
            releaseStaleKeys(mc);
            return;
        }

        boolean leftDown = Mouse.isButtonDown(0);
        boolean rightDown = Mouse.isButtonDown(1);

        // Bug #1 fix: if "Break Blocks" is off and the player is looking at a block,
        // the autoclicker should simply not click at all — no bypassing the CPS
        // system, no forcing the key state, no early-return that skips right-click.
        boolean breakBlocks = breakBlocksSetting.isCheckToggled();
        boolean lookingAtBlock = mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
        boolean leftClickAllowed = breakBlocks || !lookingAtBlock;

        // ----- Left click (attack) -----
        float leftCps = cpsSetting.getCurrentNumber();

        if (leftClickAllowed && leftDown && leftCps > 0) {
            if (System.currentTimeMillis() >= nextLeftClickTime) {
                pressKey(mc.gameSettings.keyBindAttack);
                Cloud.INSTANCE.cpsHelper.addLeftClick();

                boolean blockHit = blockhitSetting.isCheckToggled();
                if (blockHit && mc.thePlayer.getHeldItem() != null
                        && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    pressKey(mc.gameSettings.keyBindUseItem);
                }

                nextLeftClickTime = System.currentTimeMillis() + jitteredDelay(leftCps);
            }
        } else if (!leftDown) {
            // Button released — make sure the key isn't left in a held state
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }

        // ----- Right click (use item) -----
        boolean rightClickMode = rightClickModeSetting.isCheckToggled();
        float rightCps = rightCpsSetting.getCurrentNumber();

        if (rightClickMode && rightDown && rightCps > 0) {
            if (System.currentTimeMillis() >= nextRightClickTime) {
                pressKey(mc.gameSettings.keyBindUseItem);
                Cloud.INSTANCE.cpsHelper.addRightClick();
                nextRightClickTime = System.currentTimeMillis() + jitteredDelay(rightCps);
            }
        } else if (!rightDown) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        wasLeftDown = leftDown;
        wasRightDown = rightDown;
    }

    /**
     * Fires a single synthetic key press using one consistent mechanism:
     * flip the bind's held state on, then tick it once so press-count-based
     * logic (e.g. attack swing handling) registers it correctly.
     */
    private void pressKey(KeyBinding binding) {
        KeyBinding.setKeyBindState(binding.getKeyCode(), true);
        KeyBinding.onTick(binding.getKeyCode());
    }

    /**
     * Computes a humanized delay around the base CPS interval. Jitter scales
     * with a fixed proportion but is clamped to a sane minimum so high-CPS
     * settings (short base delays) still get a meaningful amount of variance
     * instead of being rounded away to near-nothing.
     */
    private long jitteredDelay(float cps) {
        double baseDelay = 1000.0 / cps;
        double jitterRange = Math.max(baseDelay / 3.0, 10.0); // at least ±5ms even at 20 CPS
        double jitter = (Math.random() - 0.5) * jitterRange;
        return (long) Math.max(1, baseDelay + jitter);
    }

    /**
     * Releases attack/use-item key state so nothing stays stuck "held"
     * (e.g. when a screen opens mid-click).
     */
    private void releaseStaleKeys(Minecraft mc) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
    }
}