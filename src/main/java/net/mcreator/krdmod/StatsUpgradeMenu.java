package net.mcreator.krdmod;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class StatsUpgradeMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xD0081018;
    private static final int PANEL_EDGE = 0x4437DAFF;
    private static final int CARD_FILL = 0x77101923;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int TEXT_ACCENT = 0x63E6FF;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;

    public StatsUpgradeMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        ServerLevelBridge.requestSync(mc);
        buttonList.clear();
        int panelX = width / 2 - 220;
        int firstColumnX = panelX + 16;
        int secondColumnX = panelX + 224;
        int firstRowY = 162;
        int secondRowY = 234;

        buttonList.add(new EscapeMenu.UILeftButton(1, firstColumnX, firstRowY, 184, 20, "Живучесть +1"));
        buttonList.add(new EscapeMenu.UILeftButton(2, secondColumnX, firstRowY, 184, 20, "Скорость +1"));
        buttonList.add(new EscapeMenu.UILeftButton(3, firstColumnX, secondRowY, 184, 20, "Урон +1"));
        buttonList.add(new EscapeMenu.UILeftButton(7, panelX + 16, height - 52, 392, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        playClick();
        if (mc == null || mc.player == null) {
            return;
        }
        if (button.id == 1) {
            mc.player.sendChatMessage("/levelstat hp");
        } else if (button.id == 2) {
            mc.player.sendChatMessage("/levelstat speed");
        } else if (button.id == 3) {
            mc.player.sendChatMessage("/levelstat damage");
        } else if (button.id == 7) {
            mc.displayGuiScreen(parent);
            return;
        }
        if (button.id >= 1 && button.id <= 3) {
            ServerLevelBridge.requestSync(mc);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int panelX = width / 2 - 220;
        int panelY = 24;
        int panelW = 440;
        int panelH = height - 48;
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, PANEL_FILL);
        drawRect(panelX, panelY, panelX + panelW, panelY + 1, PANEL_EDGE);
        drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, PANEL_EDGE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.05F, 1.05F, 1.0F);
        drawCenteredString(fontRenderer, "БОЕВЫЕ ПАРАМЕТРЫ", Math.round(width / 2F / 1.05F), Math.round(36 / 1.05F), TEXT_PRIMARY);
        GlStateManager.popMatrix();

        drawCard(panelX + 16, 68, 408, 78, "Текущая сборка");
        fontRenderer.drawStringWithShadow("Свободно очков: " + ServerLevelBridge.getStatPoints(mc), panelX + 28, 92, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow("Живучесть " + ServerLevelBridge.getHpStat(mc) + "  |  Скорость " + ServerLevelBridge.getSpeedStat(mc) + "  |  Урон " + ServerLevelBridge.getDamageStat(mc), panelX + 28, 110, TEXT_SECONDARY);
        fontRenderer.drawStringWithShadow("Здесь остались только основные боевые параметры персонажа.", panelX + 28, 128, TEXT_SECONDARY);

        drawStatCard(panelX + 16, 162, 184, 64, "Живучесть", "+" + ServerLevelBridge.getHpStat(mc), "+2 HP за очко");
        drawStatCard(panelX + 224, 162, 184, 64, "Скорость", "+" + ServerLevelBridge.getSpeedStat(mc), "быстрее передвижение");
        drawStatCard(panelX + 16, 234, 184, 64, "Урон", "+" + ServerLevelBridge.getDamageStat(mc), "сильнее ближний бой");

        fontRenderer.drawStringWithShadow("Каждое улучшение тратит 1 очко параметров и сразу уходит в LevelSystem.", panelX + 22, 318, TEXT_ACCENT);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCard(int x, int y, int w, int h, String title) {
        drawRect(x, y, x + w, y + h, CARD_FILL);
        drawRect(x, y, x + w, y + 1, 0x224DE9FF);
        fontRenderer.drawStringWithShadow(title, x + 12, y + 10, TEXT_ACCENT);
    }

    private void drawStatCard(int x, int y, int w, int h, String title, String value, String effect) {
        drawCard(x, y, w, h, title);
        fontRenderer.drawStringWithShadow("Текущий бонус: " + value, x + 12, y + 28, TEXT_PRIMARY);
        fontRenderer.drawStringWithShadow(effect, x + 12, y + 44, TEXT_SECONDARY);
    }

    private void playClick() {
        if (mc != null && mc.getSoundHandler() != null && ElementsKrdModMod.sounds.containsKey(CLICK_SOUND)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ElementsKrdModMod.sounds.get(CLICK_SOUND), 1.0F));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
