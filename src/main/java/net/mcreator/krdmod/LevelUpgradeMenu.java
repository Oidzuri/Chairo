package net.mcreator.krdmod;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class LevelUpgradeMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xD0081018;
    private static final int PANEL_EDGE = 0x4437DAFF;
    private static final int CARD_FILL = 0x77101923;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int TEXT_ACCENT = 0x63E6FF;
    private static final int TEXT_SUCCESS = 0x8EF3AE;
    private static final int TEXT_WARNING = 0xFFD36E;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;
    private EscapeMenu.UILeftButton upgradeButton;
    private String localStatus = "";

    public LevelUpgradeMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        ServerLevelBridge.requestSync(mc);
        buttonList.clear();
        int panelX = width / 2 - 210;
        int bottomY = height - 52;
        upgradeButton = new EscapeMenu.UILeftButton(1, panelX + 16, bottomY, 186, 20, "Прокачать уровень");
        buttonList.add(upgradeButton);
        buttonList.add(new EscapeMenu.UILeftButton(2, panelX + 218, bottomY, 186, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        playClick();
        if (button.id == 1) {
            if (mc != null && mc.player != null) {
                mc.player.sendChatMessage("/cl level up");
                localStatus = "Запрос отправлен. Жду ответ сервера...";
                ServerLevelBridge.requestSync(mc);
            }
        } else if (button.id == 2) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int panelX = width / 2 - 210;
        int panelY = 24;
        int panelW = 420;
        int panelH = height - 48;
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, PANEL_FILL);
        drawRect(panelX, panelY, panelX + panelW, panelY + 1, PANEL_EDGE);
        drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, PANEL_EDGE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.05F, 1.05F, 1.0F);
        drawCenteredString(fontRenderer, "СЛЕДУЮЩИЙ УРОВЕНЬ", Math.round(width / 2F / 1.05F), Math.round(36 / 1.05F), TEXT_PRIMARY);
        GlStateManager.popMatrix();

        int level = ServerLevelBridge.getLevel(mc);
        int progress = ServerLevelBridge.getProgressPercent(mc);
        int currentXp = ServerLevelBridge.getCurrentXp(mc);
        int nextXp = ServerLevelBridge.getNextLevelXpRequirement(mc);
        String rank = ServerLevelBridge.getRank(mc);
        String quest = ServerLevelBridge.getNextLevelQuest(mc);
        String items = ServerLevelBridge.getNextLevelItems(mc);
        boolean ready = ServerLevelBridge.isLevelUpReady(mc);
        String status = ServerLevelBridge.getLevelStatus(mc);
        if (status == null || status.trim().isEmpty()) {
            status = localStatus;
        }

        drawCard(panelX + 16, 72, 188, 106, "Текущий прогресс");
        fontRenderer.drawStringWithShadow("Сейчас: Lv. " + level, panelX + 28, 96, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Ранг: " + rank, panelX + 28, 114, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Прогресс: " + progress + "%", panelX + 28, 132, TEXT_SECONDARY);
        fontRenderer.drawStringWithShadow("XP: " + currentXp + " / " + nextXp, panelX + 28, 150, TEXT_SECONDARY);

        drawCard(panelX + 216, 72, 188, 106, "Награды за ап");
        fontRenderer.drawStringWithShadow("+ " + Math.max(0, ServerLevelBridge.getRewardStatPoints(mc)) + " очк. параметров", panelX + 228, 96, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("+ " + Math.max(0, ServerLevelBridge.getRewardSkillPoints(mc)) + " очк. техник", panelX + 228, 114, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Открывается следующий уровень", panelX + 228, 132, TEXT_SECONDARY);
        fontRenderer.drawStringWithShadow(ready ? "Можно повышать уже сейчас" : "Сервер еще ждёт условия", panelX + 228, 150,
                ready ? TEXT_SUCCESS : TEXT_WARNING);

        drawCard(panelX + 16, 186, 388, 148, "Что нужно выполнить");
        fontRenderer.drawStringWithShadow("1. Набрать нужный опыт: " + currentXp + " / " + nextXp, panelX + 28, 210,
                currentXp >= nextXp && nextXp > 0 ? TEXT_SUCCESS : TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("2. Квест: " + (quest.isEmpty() ? "не требуется" : quest), panelX + 28, 228,
                quest.isEmpty() ? TEXT_SUCCESS : TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("3. Предметы: " + (items.isEmpty() ? "не требуются" : items), panelX + 28, 246,
                items.isEmpty() ? TEXT_SUCCESS : TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("4. Награда: +" + ServerLevelBridge.getRewardStatPoints(mc) + " очк. параметров, +"
                + ServerLevelBridge.getRewardSkillPoints(mc) + " очк. техник", panelX + 28, 270, TEXT_SECONDARY);
        fontRenderer.drawStringWithShadow(ready ? "Статус: все условия выполнены" : "Статус: пока не все условия закрыты", panelX + 28, 288,
                ready ? TEXT_SUCCESS : TEXT_WARNING);
        if (!status.trim().isEmpty()) {
            drawWrappedString(status, panelX + 28, 306, 360, ready ? TEXT_SUCCESS : TEXT_WARNING);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCard(int x, int y, int w, int h, String title) {
        drawRect(x, y, x + w, y + h, CARD_FILL);
        drawRect(x, y, x + w, y + 1, 0x224DE9FF);
        fontRenderer.drawStringWithShadow(title, x + 12, y + 10, TEXT_ACCENT);
    }

    private void playClick() {
        if (mc != null && mc.getSoundHandler() != null && ElementsKrdModMod.sounds.containsKey(CLICK_SOUND)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ElementsKrdModMod.sounds.get(CLICK_SOUND), 1.0F));
        }
    }

    private void drawWrappedString(String text, int x, int y, int maxWidth, int color) {
        int lineY = y;
        for (String line : fontRenderer.listFormattedStringToWidth(text, maxWidth)) {
            fontRenderer.drawStringWithShadow(line, x, lineY, color);
            lineY += 12;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
