package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class ProfileMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xD0081018;
    private static final int PANEL_EDGE = 0x4437DAFF;
    private static final int CARD_FILL = 0x77101923;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int TEXT_ACCENT = 0x63E6FF;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;

    public ProfileMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        ServerLevelBridge.requestSync(mc);
        buttonList.clear();
        int centerX = width / 2;
        int baseY = Math.max(height / 2 + 4, 176);
        buttonList.add(new EscapeMenu.UILeftButton(1, centerX - 112, baseY, 224, 20, "Дыхания и техники"));
        buttonList.add(new EscapeMenu.UILeftButton(2, centerX - 112, baseY + 24, 224, 20, "Бинды навыков"));
        buttonList.add(new EscapeMenu.UILeftButton(3, centerX - 112, baseY + 48, 224, 20, "Уровень и награды"));
        buttonList.add(new EscapeMenu.UILeftButton(5, centerX - 112, baseY + 72, 224, 20, "Боевые параметры"));
        buttonList.add(new EscapeMenu.UILeftButton(6, centerX - 112, baseY + 96, 224, 20, "Персонализация"));
        buttonList.add(new EscapeMenu.UILeftButton(4, centerX - 112, baseY + 120, 224, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        playClick();
        if (button.id == 1) {
            mc.displayGuiScreen(new MyBreathingsMenu(this));
        } else if (button.id == 2) {
            mc.displayGuiScreen(new SkillBindingsMenu(this));
        } else if (button.id == 3) {
            mc.displayGuiScreen(new LevelUpgradeMenu(this));
        } else if (button.id == 5) {
            mc.displayGuiScreen(new StatsUpgradeMenu(this));
        } else if (button.id == 6) {
            openKrdArmCustomization();
        } else if (button.id == 4) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int panelX = width / 2 - 170;
        int panelY = 28;
        int panelW = 340;
        int panelH = height - 56;
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, PANEL_FILL);
        drawRect(panelX, panelY, panelX + panelW, panelY + 1, PANEL_EDGE);
        drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, PANEL_EDGE);
        drawRect(panelX, panelY, panelX + 1, panelY + panelH, PANEL_EDGE);
        drawRect(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, PANEL_EDGE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.08F, 1.08F, 1.0F);
        drawCenteredString(fontRenderer, "БОЕВОЙ ПРОФИЛЬ", Math.round(width / 2F / 1.08F), Math.round(38 / 1.08F), TEXT_PRIMARY);
        GlStateManager.popMatrix();

        EntityPlayerSP player = mc.player;
        String playerName = player != null ? player.getName() : "Игрок";
        drawCenteredString(fontRenderer, playerName, width / 2, 60, TEXT_ACCENT);

        drawInfoCard(panelX + 18, 82, 144, 122, "Прогресс");
        drawInfoCard(panelX + 178, 82, 144, 122, "Параметры");
        drawInfoCard(panelX + 18, 212, 304, 70, "Роли");

        int level = ServerLevelBridge.getLevel(mc);
        int progress = ServerLevelBridge.getProgressPercent(mc);
        String rank = ServerLevelBridge.getRank(mc);
        fontRenderer.drawStringWithShadow("Уровень: " + level, panelX + 30, 104, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Опыт: " + progress + "%", panelX + 30, 122, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Ранг: " + rank, panelX + 30, 140, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Дыханий: " + ServerLevelBridge.getOwnedBreaths(mc).size(), panelX + 30, 158, TEXT_SECONDARY);
        fontRenderer.drawStringWithShadow("Биндов: " + SkillBindingManager.getBoundDefinitions().size(), panelX + 30, 176, TEXT_SECONDARY);

        if (player != null) {
            fontRenderer.drawStringWithShadow("HP: +" + ServerLevelBridge.getHpStat(mc), panelX + 190, 104, TEXT_PRIMARY);
            fontRenderer.drawStringWithShadow("Скорость: +" + ServerLevelBridge.getSpeedStat(mc), panelX + 190, 122, TEXT_PRIMARY);
            fontRenderer.drawStringWithShadow("Урон: +" + ServerLevelBridge.getDamageStat(mc), panelX + 190, 140, TEXT_PRIMARY);
            fontRenderer.drawStringWithShadow("Очки статов: " + ServerLevelBridge.getStatPoints(mc), panelX + 190, 158, TEXT_SECONDARY);
            fontRenderer.drawStringWithShadow("Очки навыков: " + ServerLevelBridge.getSkillPoints(mc), panelX + 190, 176, TEXT_SECONDARY);
        }

        fontRenderer.drawStringWithShadow("Профессия: " + ServerLevelBridge.getProfession(), panelX + 30, 234, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Ранг проф.: " + ServerLevelBridge.getProfessionRank(), panelX + 30, 252, TEXT_SECONDARY);
        fontRenderer.drawStringWithShadow("Клан: " + ServerLevelBridge.getClan(), panelX + 190, 234, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Адм. роль: " + (ServerLevelBridge.getAdminRole().isEmpty() ? "Нет" : ServerLevelBridge.getAdminRole()), panelX + 190, 252, TEXT_SECONDARY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawInfoCard(int x, int y, int w, int h, String title) {
        drawRect(x, y, x + w, y + h, CARD_FILL);
        drawRect(x, y, x + w, y + 1, 0x224DE9FF);
        fontRenderer.drawStringWithShadow(title, x + 12, y + 10, TEXT_ACCENT);
    }

    private void playClick() {
        if (mc != null && mc.getSoundHandler() != null && ElementsKrdModMod.sounds.containsKey(CLICK_SOUND)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ElementsKrdModMod.sounds.get(CLICK_SOUND), 1.0F));
        }
    }

    private void openKrdArmCustomization() {
        KRDClientInterop.openKrdArmCustomization(this);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
