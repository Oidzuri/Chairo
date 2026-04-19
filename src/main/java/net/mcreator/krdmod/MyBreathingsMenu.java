package net.mcreator.krdmod;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class MyBreathingsMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xD0081018;
    private static final int PANEL_EDGE = 0x4437DAFF;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int TEXT_ACCENT = 0x63E6FF;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;
    private int scroll;
    private int maxScroll;
    private int visibleRows;
    private int rowTop;
    private int rowSpacing;
    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    public MyBreathingsMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        rebuildButtons();
    }

    private void rebuildButtons() {
        buttonList.clear();
        List<String> breaths = ServerLevelBridge.getOwnedBreaths(mc);
        panelX = width / 2 - 176;
        panelY = 30;
        panelW = 352;
        panelH = height - 60;
        rowTop = panelY + 72;
        int availableHeight = Math.max(140, panelH - 122);
        visibleRows = Math.max(1, Math.min(Math.max(4, availableHeight / 44), breaths.size() == 0 ? 4 : breaths.size()));
        rowSpacing = Math.max(40, Math.min(48, availableHeight / Math.max(1, visibleRows)));
        int visible = Math.min(visibleRows, breaths.size());
        maxScroll = Math.max(0, breaths.size() - visible);
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        for (int i = 0; i < visible; i++) {
            String breathId = breaths.get(i + scroll);
            String title = getBreathTitle(breathId);
            buttonList.add(new EscapeMenu.UILeftButton(100 + i, width / 2 - 120, rowTop + 2 + i * rowSpacing, 240, 20, title));
        }
        buttonList.add(new EscapeMenu.UILeftButton(1, width / 2 - 120, panelY + panelH - 28, 240, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            playClick();
            mc.displayGuiScreen(parent);
            return;
        }

        List<String> breaths = ServerLevelBridge.getOwnedBreaths(mc);
        int index = button.id - 100 + scroll;
        if (index >= 0 && index < breaths.size()) {
            playClick();
            mc.displayGuiScreen(new BreathSkillsMenu(this, breaths.get(index)));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel > 0 && scroll > 0) {
            scroll--;
            rebuildButtons();
        } else if (wheel < 0 && scroll < maxScroll) {
            scroll++;
            rebuildButtons();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, PANEL_FILL);
        drawRect(panelX, panelY, panelX + panelW, panelY + 1, PANEL_EDGE);
        drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, PANEL_EDGE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.05F, 1.05F, 1.0F);
        drawCenteredString(fontRenderer, "МОИ ДЫХАНИЯ", Math.round(width / 2F / 1.05F), Math.round(38 / 1.05F), TEXT_PRIMARY);
        GlStateManager.popMatrix();

        List<String> breaths = ServerLevelBridge.getOwnedBreaths(mc);
        if (breaths.isEmpty()) {
            drawCenteredString(fontRenderer, "У вас пока нет выданных дыханий.", width / 2, 108, TEXT_SECONDARY);
            drawCenteredString(fontRenderer, "Админ может выдать их через /levelbreath add <игрок> <voda|grom|veter|zmei|plamya>", width / 2, 124, TEXT_SECONDARY);
        } else {
            drawCenteredString(fontRenderer, "Колесо мыши листает список вниз.", width / 2, 82, TEXT_ACCENT);
            int shown = Math.min(visibleRows, breaths.size() - scroll);
            for (int i = 0; i < shown; i++) {
                String breathId = breaths.get(i + scroll);
                ResourceLocation icon = SkillBindingManager.getBreathIcon(breathId);
                int rowY = rowTop + i * rowSpacing;
                drawIcon(icon, width / 2 - 152, rowY, 24);
                String title = getBreathShortTitle(breathId);
                fontRenderer.drawStringWithShadow(title, width / 2 - 118, rowY + 6, TEXT_PRIMARY);
                fontRenderer.drawStringWithShadow("Очки навыков: " + ServerLevelBridge.getSkillPoints(mc), width / 2 - 118, rowY + 20, TEXT_SECONDARY);
            }
            drawScrollBar(panelX + panelW - 8, rowTop, panelH - (rowTop - panelY) - 48, breaths.size(), shown);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawIcon(ResourceLocation icon, int x, int y, int size) {
        mc.getTextureManager().bindTexture(icon);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
    }

    private String getBreathTitle(String breathId) {
        if ("voda".equalsIgnoreCase(breathId)) {
            return "Дыхание воды";
        }
        if ("grom".equalsIgnoreCase(breathId)) {
            return "Дыхание грома";
        }
        if ("veter".equalsIgnoreCase(breathId)) {
            return "Дыхание ветра";
        }
        if ("zmei".equalsIgnoreCase(breathId)) {
            return "Дыхание змеи";
        }
        if ("plamya".equalsIgnoreCase(breathId)) {
            return "Дыхание пламени";
        }
        return breathId;
    }

    private String getBreathShortTitle(String breathId) {
        if ("voda".equalsIgnoreCase(breathId)) {
            return "Вода";
        }
        if ("grom".equalsIgnoreCase(breathId)) {
            return "Гром";
        }
        if ("veter".equalsIgnoreCase(breathId)) {
            return "Ветер";
        }
        if ("zmei".equalsIgnoreCase(breathId)) {
            return "Змея";
        }
        if ("plamya".equalsIgnoreCase(breathId)) {
            return "Пламя";
        }
        return breathId;
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

    private void drawScrollBar(int x, int y, int height, int totalItems, int shownItems) {
        if (totalItems <= shownItems) {
            return;
        }
        drawRect(x, y, x + 3, y + height, 0x33000000);
        int thumbHeight = Math.max(12, height * shownItems / totalItems);
        int maxOffset = Math.max(1, totalItems - shownItems);
        int thumbY = y + (height - thumbHeight) * scroll / maxOffset;
        drawRect(x, thumbY, x + 3, thumbY + thumbHeight, TEXT_ACCENT);
    }
}
