package net.mcreator.krdmod;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillBindingsMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xD0091118;
    private static final int PANEL_EDGE = 0x4437DAFF;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int TEXT_ACCENT = 0x63E6FF;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;
    private final Map<Integer, String> buttonToSkill = new HashMap<>();
    private String listeningSkillId;
    private int scroll;
    private int maxScroll;
    private int visibleRows;

    public SkillBindingsMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        ServerLevelBridge.requestSync(mc);
        rebuildButtons();
    }

    private void rebuildButtons() {
        buttonList.clear();
        buttonToSkill.clear();

        List<SkillBindingManager.SkillDefinition> skills = SkillBindingManager.getUnlockedDefinitions(mc);
        visibleRows = Math.max(1, Math.min(Math.max(6, (height - 170) / 38), skills.size() == 0 ? 6 : skills.size()));
        int visible = Math.min(visibleRows, skills.size());
        maxScroll = Math.max(0, skills.size() - visible);
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        for (int i = 0; i < visible; i++) {
            SkillBindingManager.SkillDefinition definition = skills.get(i + scroll);
            GuiButton button = new EscapeMenu.UILeftButton(100 + i, width / 2 + 34, 104 + i * 38, 92, 18, getButtonLabel(definition.getId()));
            buttonList.add(button);
            buttonToSkill.put(button.id, definition.getId());
        }

        int panelBottom = height - 30;
        buttonList.add(new EscapeMenu.UILeftButton(1, width / 2 - 120, panelBottom - 48, 240, 20, "Сбросить все бинды"));
        buttonList.add(new EscapeMenu.UILeftButton(2, width / 2 - 120, panelBottom - 24, 240, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            playClick();
            SkillBindingManager.clearAll();
            listeningSkillId = null;
            rebuildButtons();
            return;
        }
        if (button.id == 2) {
            playClick();
            mc.displayGuiScreen(parent);
            return;
        }

        String skillId = buttonToSkill.get(button.id);
        if (skillId != null) {
            playClick();
            listeningSkillId = skillId;
            rebuildButtons();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (listeningSkillId != null) {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK) {
                SkillBindingManager.setKeyCode(listeningSkillId, Keyboard.KEY_NONE);
            } else if (keyCode != Keyboard.KEY_NONE) {
                SkillBindingManager.setKeyCode(listeningSkillId, keyCode);
            }
            listeningSkillId = null;
            rebuildButtons();
            return;
        }
        super.keyTyped(typedChar, keyCode);
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

        int panelX = width / 2 - 176;
        int panelY = 30;
        int panelW = 352;
        int panelH = height - 60;
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, PANEL_FILL);
        drawRect(panelX, panelY, panelX + panelW, panelY + 1, PANEL_EDGE);
        drawRect(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, PANEL_EDGE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.05F, 1.05F, 1.0F);
        drawCenteredString(fontRenderer, "БИНДЫ НАВЫКОВ", Math.round(width / 2F / 1.05F), Math.round(38 / 1.05F), TEXT_PRIMARY);
        GlStateManager.popMatrix();
        drawCenteredString(fontRenderer, "Показываются только уже открытые навыки. Одна кнопка = один навык.", width / 2, 66, TEXT_SECONDARY);
        drawCenteredString(fontRenderer, "Колесо мыши листает список вниз.", width / 2, 82, TEXT_ACCENT);

        List<SkillBindingManager.SkillDefinition> skills = SkillBindingManager.getUnlockedDefinitions(mc);
        if (skills.isEmpty()) {
            drawCenteredString(fontRenderer, "Сначала открой хотя бы одну технику в разделе дыханий.", width / 2, 118, TEXT_SECONDARY);
        } else {
            int shown = Math.min(visibleRows, skills.size() - scroll);
            for (int i = 0; i < shown; i++) {
                SkillBindingManager.SkillDefinition definition = skills.get(i + scroll);
                int rowY = 104 + i * 38;
                drawIcon(definition.getKataIcon(), width / 2 - 124, rowY, 20);
                fontRenderer.drawStringWithShadow(definition.getFamilyName() + " • " + definition.getDisplayName(), width / 2 - 96, rowY + 2, TEXT_PRIMARY);
                fontRenderer.drawStringWithShadow("КД: " + SkillBindingManager.getCooldownLabel(definition.getId()), width / 2 - 96, rowY + 13, TEXT_SECONDARY);
            }
            drawScrollBar(panelX + panelW - 8, 102, panelH - 146, skills.size(), shown);
        }

        if (listeningSkillId != null) {
            drawCenteredString(fontRenderer, "Нажмите клавишу для " + listeningSkillId, width / 2, height - 82, TEXT_ACCENT);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getButtonLabel(String skillId) {
        if (skillId.equals(listeningSkillId)) {
            return "Жду клавишу";
        }
        return SkillBindingManager.getKeyName(skillId);
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
