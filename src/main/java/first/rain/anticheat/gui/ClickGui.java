package first.rain.anticheat.gui;

import first.rain.anticheat.config.cfg;
import first.rain.anticheat.util.RenderUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {
   private static final int CARD_W = 120;
   private static final int CARD_H = 72;
   private static final int GAP = 8;
   private static final int PADDING = 10;
   private static final int TITLE_BAR_HEIGHT = 20;
   private static final int TAB_BAR_HEIGHT = 18;
   private static final int CONTENT_HEIGHT = 104;
   private static final int FLASH_CARD_HEIGHT = 100;
   private static final int NAMETAG_CARD_HEIGHT = 104;
   private static final int TAB_GAP = 24;
   private static final int TAB_ALERTS = 0;
   private static final int TAB_NOTIFICATIONS = 1;
   private static final int TAB_NAMETAGS = 2;

   private static int panelX = Integer.MIN_VALUE;
   private static int panelY = Integer.MIN_VALUE;
   private static int activeTab = TAB_ALERTS;

   private final List<ModuleCard> alertCards = new ArrayList<ModuleCard>();
   private final FlashSettingsCard flashCard;
   private final NametagSettingsCard nametagCard;
   private final ModuleCard debugCard;
   private final int panelWidth;
   private final int panelHeight;
   private boolean dragging;
   private int dragOffsetX;
   private int dragOffsetY;
   private long lastFrameMillis;
   private float tabAnim;

   public ClickGui() {
      this.alertCards.add(new ModuleCard("AutoBlock", "Swinging while sword-blocking.",
         () -> cfg.v.detectAutoBlock, (v) -> cfg.v.detectAutoBlock = v, CARD_W, CARD_H));
      this.alertCards.add(new ModuleCard("LegitScaffold", "Robotic crouch-bridge rhythm.",
         () -> cfg.v.detectLegitScaffold, (v) -> cfg.v.detectLegitScaffold = v, CARD_W, CARD_H));
      this.alertCards.add(new ModuleCard("Killaura", "Silent/snap aim, robotic rotations, eating mid-swing.",
         () -> cfg.v.detectKillaura, (v) -> cfg.v.detectKillaura = v, CARD_W, CARD_H));

      this.flashCard = new FlashSettingsCard(CARD_W * 2 + GAP, FLASH_CARD_HEIGHT);
      this.nametagCard = new NametagSettingsCard(CARD_W * 3 + GAP * 2, NAMETAG_CARD_HEIGHT);
      this.debugCard = new ModuleCard("Debug Messages", "Verbose check output in chat.",
         () -> cfg.v.debugMessages, (v) -> cfg.v.debugMessages = v, CARD_W, CARD_H);

      this.panelWidth = PADDING * 2 + 3 * CARD_W + 2 * GAP;
      this.panelHeight = TITLE_BAR_HEIGHT + TAB_BAR_HEIGHT + PADDING + CONTENT_HEIGHT + PADDING;
      this.tabAnim = activeTab;
   }

   @Override
   public void initGui() {
      if (panelX == Integer.MIN_VALUE) {
         panelX = (this.width - this.panelWidth) / 2;
         panelY = (this.height - this.panelHeight) / 2;
      }
      this.clampPanel();
      this.lastFrameMillis = System.currentTimeMillis();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      long now = System.currentTimeMillis();
      float deltaSeconds = Math.min((float)(now - this.lastFrameMillis) / 1000.0F, 0.1F);
      this.lastFrameMillis = now;

      if (this.dragging) {
         panelX = mouseX - this.dragOffsetX;
         panelY = mouseY - this.dragOffsetY;
         this.clampPanel();
      }

      float tabTarget = activeTab;
      this.tabAnim += (tabTarget - this.tabAnim) * Math.min(1.0F, deltaSeconds * 10.0F);
      if (Math.abs(tabTarget - this.tabAnim) < 0.01F) {
         this.tabAnim = tabTarget;
      }

      drawRect(0, 0, this.width, this.height, 0x88000000);
      RenderUtil.drawRoundedShadow(panelX, panelY, this.panelWidth, this.panelHeight, 7.0F);
      RenderUtil.drawRoundedRect(panelX, panelY, this.panelWidth, this.panelHeight, 7.0F, 0xFA0C0C0C);
      RenderUtil.drawRoundedOutline(panelX, panelY, this.panelWidth, this.panelHeight, 7.0F, 1.0F, 0x46FFFFFF);

      int titleTextY = panelY + (TITLE_BAR_HEIGHT - 8) / 2;
      this.fontRendererObj.drawStringWithShadow("AntiCheat", (float)(panelX + PADDING), (float)titleTextY, 0xFFFFFFFF);
      String hint = "BETA";
      this.fontRendererObj.drawString(hint, panelX + this.panelWidth - PADDING - this.fontRendererObj.getStringWidth(hint), titleTextY, 0xFF5A5A5A);

      this.drawTabs(mouseX, mouseY);

      float alertsAlpha = this.pageAlpha(TAB_ALERTS);
      float notifAlpha = this.pageAlpha(TAB_NOTIFICATIONS);
      float nametagAlpha = this.pageAlpha(TAB_NAMETAGS);
      int contentY = panelY + TITLE_BAR_HEIGHT + TAB_BAR_HEIGHT + PADDING;
      int alertsMouseX = activeTab == TAB_ALERTS ? mouseX : -9999;
      int notifMouseX = activeTab == TAB_NOTIFICATIONS ? mouseX : -9999;
      int nametagMouseX = activeTab == TAB_NAMETAGS ? mouseX : -9999;

      if (alertsAlpha > 0.02F) {
         for (int i = 0; i < this.alertCards.size(); ++i) {
            ModuleCard card = this.alertCards.get(i);
            card.setPosition(panelX + PADDING + (i % 3) * (CARD_W + GAP), contentY + (i / 3) * (CARD_H + GAP));
            card.render(this.fontRendererObj, alertsMouseX, mouseY, deltaSeconds, alertsAlpha);
         }
      }
      if (notifAlpha > 0.02F) {
         this.flashCard.setPosition(panelX + PADDING, contentY);
         this.flashCard.render(this.fontRendererObj, notifMouseX, mouseY, deltaSeconds, notifAlpha);
         this.debugCard.setPosition(panelX + PADDING + 2 * (CARD_W + GAP), contentY);
         this.debugCard.render(this.fontRendererObj, notifMouseX, mouseY, deltaSeconds, notifAlpha);
      }
      if (nametagAlpha > 0.02F) {
         this.nametagCard.setPosition(panelX + PADDING, contentY);
         this.nametagCard.render(this.fontRendererObj, nametagMouseX, mouseY, deltaSeconds, nametagAlpha);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private void drawTabs(int mouseX, int mouseY) {
      int[] tabs = this.tabLayout();
      int tabTextY = panelY + TITLE_BAR_HEIGHT + (TAB_BAR_HEIGHT - 8) / 2;
      boolean hoverAlerts = this.isOverTab(mouseX, mouseY, tabs[0], tabs[1]);
      boolean hoverNotif = this.isOverTab(mouseX, mouseY, tabs[2], tabs[3]);
      boolean hoverNametag = this.isOverTab(mouseX, mouseY, tabs[4], tabs[5]);

      int alertsColor = activeTab == TAB_ALERTS ? 0xFFFFFFFF : (hoverAlerts ? 0xFFB4B4B4 : 0xFF6E6E6E);
      int notifColor = activeTab == TAB_NOTIFICATIONS ? 0xFFFFFFFF : (hoverNotif ? 0xFFB4B4B4 : 0xFF6E6E6E);
      int nametagColor = activeTab == TAB_NAMETAGS ? 0xFFFFFFFF : (hoverNametag ? 0xFFB4B4B4 : 0xFF6E6E6E);
      this.fontRendererObj.drawString("Alerts", tabs[0], tabTextY, alertsColor);
      this.fontRendererObj.drawString("Notifications", tabs[2], tabTextY, notifColor);
      this.fontRendererObj.drawString("Nametags", tabs[4], tabTextY, nametagColor);

      int lineY = panelY + TITLE_BAR_HEIGHT + TAB_BAR_HEIGHT - 2;
      drawRect(panelX + PADDING, lineY, panelX + this.panelWidth - PADDING, lineY + 1, 0x1EFFFFFF);
      int fromTab = Math.max(0, Math.min(TAB_NAMETAGS, (int)Math.floor(this.tabAnim)));
      int toTab = Math.max(0, Math.min(TAB_NAMETAGS, fromTab + 1));
      float t = this.tabAnim - (float)fromTab;
      float indicatorX = this.tabX(tabs, fromTab) + (this.tabX(tabs, toTab) - this.tabX(tabs, fromTab)) * t;
      float indicatorW = this.tabW(tabs, fromTab) + (this.tabW(tabs, toTab) - this.tabW(tabs, fromTab)) * t;
      RenderUtil.drawRoundedRect(indicatorX - 2.0F, (float)(lineY - 1), indicatorW + 4.0F, 2.0F, 1.0F, 0xFFFFFFFF);
   }

   private int[] tabLayout() {
      int alertsW = this.fontRendererObj.getStringWidth("Alerts");
      int notifW = this.fontRendererObj.getStringWidth("Notifications");
      int nametagW = this.fontRendererObj.getStringWidth("Nametags");
      int totalW = alertsW + notifW + nametagW + TAB_GAP * 2;
      int baseX = panelX + (this.panelWidth - totalW) / 2;
      int notifX = baseX + alertsW + TAB_GAP;
      int nametagX = notifX + notifW + TAB_GAP;
      return new int[]{baseX, alertsW, notifX, notifW, nametagX, nametagW};
   }

   private float pageAlpha(int tab) {
      return Math.max(0.0F, 1.0F - Math.min(1.0F, Math.abs(this.tabAnim - (float)tab)));
   }

   private int tabX(int[] tabs, int tab) {
      return tabs[tab * 2];
   }

   private int tabW(int[] tabs, int tab) {
      return tabs[tab * 2 + 1];
   }

   private boolean isOverTab(int mouseX, int mouseY, int tabX, int tabW) {
      return mouseY >= panelY + TITLE_BAR_HEIGHT && mouseY < panelY + TITLE_BAR_HEIGHT + TAB_BAR_HEIGHT
         && mouseX >= tabX - 6 && mouseX < tabX + tabW + 6;
   }

   private void setTab(int tab) {
      if (activeTab != tab) {
         activeTab = tab;
         this.mc.getSoundHandler().playSound(
            PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton != 0) {
         return;
      }
      if (mouseX >= panelX && mouseX < panelX + this.panelWidth && mouseY >= panelY && mouseY < panelY + TITLE_BAR_HEIGHT) {
         this.dragging = true;
         this.dragOffsetX = mouseX - panelX;
         this.dragOffsetY = mouseY - panelY;
         return;
      }

      int[] tabs = this.tabLayout();
      if (this.isOverTab(mouseX, mouseY, tabs[0], tabs[1])) {
         this.setTab(TAB_ALERTS);
         return;
      }
      if (this.isOverTab(mouseX, mouseY, tabs[2], tabs[3])) {
         this.setTab(TAB_NOTIFICATIONS);
         return;
      }
      if (this.isOverTab(mouseX, mouseY, tabs[4], tabs[5])) {
         this.setTab(TAB_NAMETAGS);
         return;
      }

      if (activeTab == TAB_ALERTS) {
         for (ModuleCard card : this.alertCards) {
            if (card.mouseClicked(mouseX, mouseY, mouseButton)) {
               return;
            }
         }
      } else if (activeTab == TAB_NOTIFICATIONS) {
         if (this.flashCard.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
         }
         this.debugCard.mouseClicked(mouseX, mouseY, mouseButton);
      } else if (activeTab == TAB_NAMETAGS) {
         this.nametagCard.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      if (activeTab == TAB_NOTIFICATIONS) {
         this.flashCard.mouseDragged(mouseX, mouseY);
      } else if (activeTab == TAB_NAMETAGS) {
         this.nametagCard.mouseDragged(mouseX, mouseY);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      this.dragging = false;
      this.flashCard.mouseReleased();
      this.nametagCard.mouseReleased();
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode != 0 && keyCode == ClickGuiKeybind.OPEN_GUI.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
         return;
      }
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void onGuiClosed() {
      this.dragging = false;
      this.flashCard.mouseReleased();
      this.nametagCard.mouseReleased();
      cfg.save();
   }

   private void clampPanel() {
      panelX = Math.max(0, Math.min(panelX, this.width - this.panelWidth));
      panelY = Math.max(0, Math.min(panelY, this.height - this.panelHeight));
   }
}
