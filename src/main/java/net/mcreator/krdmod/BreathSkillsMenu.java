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

public class BreathSkillsMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xD0081018;
    private static final int PANEL_EDGE = 0x4437DAFF;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int TEXT_ACCENT = 0x63E6FF;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;
    private final String breathId;
    private int scroll;
    private int maxScroll;
    private int visibleRows;
    private int rowTop;
    private int rowSpacing;
    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    public BreathSkillsMenu(GuiScreen parent, String breathId) {
        this.parent = parent;
        this.breathId = breathId;
    }

    @Override
    public void initGui() {
        rebuildButtons();
    }

    private void rebuildButtons() {
        buttonList.clear();
        List<String> skills = ServerLevelBridge.getAllSkillsForBreath(breathId);
        panelX = width / 2 - 176;
        panelY = 30;
        panelW = 352;
        panelH = height - 60;
        rowTop = panelY + 74;
        int availableHeight = Math.max(160, panelH - 120);
        visibleRows = Math.max(1, Math.min(Math.max(5, availableHeight / 36), skills.size() == 0 ? 5 : skills.size()));
        rowSpacing = Math.max(34, Math.min(38, availableHeight / Math.max(1, visibleRows)));
        int visible = Math.min(visibleRows, skills.size());
        maxScroll = Math.max(0, skills.size() - visible);
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        for (int i = 0; i < visible; i++) {
            String skillId = skills.get(i + scroll);
            int level = ServerLevelBridge.getSkillLevel(mc, skillId);
            String action = level <= 0 ? "Открыть" : (level >= 3 ? "Макс" : "Улучшить");
            EscapeMenu.UILeftButton button = new EscapeMenu.UILeftButton(100 + i, width / 2 + 26, rowTop + i * rowSpacing, 96, 18, action);
            button.enabled = level < 3;
            buttonList.add(button);
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

        List<String> skills = ServerLevelBridge.getAllSkillsForBreath(breathId);
        int index = button.id - 100 + scroll;
        if (index >= 0 && index < skills.size() && mc != null && mc.player != null) {
            int level = ServerLevelBridge.getSkillLevel(mc, skills.get(index));
            if (level >= 3) {
                return;
            }
            playClick();
            mc.player.sendChatMessage("/levelskill upgrade " + skills.get(index));
            ServerLevelBridge.applyLocalSkillUpgrade(skills.get(index));
            rebuildButtons();
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

        String title = getBreathTitle(breathId);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.05F, 1.05F, 1.0F);
        drawCenteredString(fontRenderer, title, Math.round(width / 2F / 1.05F), Math.round(38 / 1.05F), TEXT_PRIMARY);
        GlStateManager.popMatrix();
        drawCenteredString(fontRenderer, "Очки навыков: " + ServerLevelBridge.getSkillPoints(mc), width / 2, 66, TEXT_ACCENT);
        drawCenteredString(fontRenderer, "Колесо мыши листает список техник.", width / 2, 82, TEXT_SECONDARY);

        List<String> skills = ServerLevelBridge.getAllSkillsForBreath(breathId);
        int shown = Math.min(visibleRows, skills.size() - scroll);
        for (int i = 0; i < shown; i++) {
            String skillId = skills.get(i + scroll);
            int level = ServerLevelBridge.getSkillLevel(mc, skillId);
            int rowY = rowTop + i * rowSpacing;
            Gui.drawRect(width / 2 - 130, rowY - 2, width / 2 + 128, rowY + 24, 0x66101923);
            drawIcon(getSkillIcon(skillId), width / 2 - 124, rowY, 20);
            fontRenderer.drawStringWithShadow(getSkillTitle(skillId), width / 2 - 96, rowY + 2, TEXT_PRIMARY);
            String state = level <= 0 ? "Не открыт" : ("Уровень: " + level + " / 3");
            fontRenderer.drawStringWithShadow(state, width / 2 - 96, rowY + 13, TEXT_SECONDARY);
        }
        drawScrollBar(panelX + panelW - 8, rowTop, panelH - (rowTop - panelY) - 48, skills.size(), shown);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getSkillTitle(String skillId) {
        SkillBindingManager.SkillDefinition definition = SkillBindingManager.getDefinition(skillId);
        if (definition != null) {
            return definition.getDisplayName();
        }
        String number = skillId.replace("wk", "").replace("lk", "").replace("vk", "").replace("zk", "").replace("fk", "");
        return "Ката " + number;
    }

    private ResourceLocation getSkillIcon(String skillId) {
        SkillBindingManager.SkillDefinition definition = SkillBindingManager.getDefinition(skillId);
        if (definition != null) {
            return definition.getKataIcon();
        }
        if (skillId.startsWith("wk")) {
            if ("wk10".equals(skillId)) {
                return new ResourceLocation("krd_mod", "textures/breath/voda.png");
            }
            return new ResourceLocation("krd_mod", "textures/katas/kata_voda_item" + skillId.substring(2) + ".png");
        }
        return new ResourceLocation("krd_mod", "textures/katas/kata_grom_item" + skillId.substring(2) + ".png");
    }

    private String getBreathTitle(String breath) {
        if ("voda".equalsIgnoreCase(breath)) {
            return "ДЫХАНИЕ ВОДЫ";
        }
        if ("grom".equalsIgnoreCase(breath)) {
            return "ДЫХАНИЕ ГРОМА";
        }
        if ("veter".equalsIgnoreCase(breath)) {
            return "ДЫХАНИЕ ВЕТРА";
        }
        if ("zmei".equalsIgnoreCase(breath)) {
            return "ДЫХАНИЕ ЗМЕИ";
        }
        if ("plamya".equalsIgnoreCase(breath)) {
            return "ДЫХАНИЕ ПЛАМЕНИ";
        }
        return breath.toUpperCase();
    }

    private void drawIcon(ResourceLocation icon, int x, int y, int size) {
        mc.getTextureManager().bindTexture(icon);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
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
