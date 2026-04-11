package net.mcreator.krdmod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class SkillBindingsMenu extends GuiScreen {
    private static final int PANEL_FILL = 0xCC071018;
    private static final int PANEL_EDGE = 0x3300E5FF;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");

    private final GuiScreen parent;

    public SkillBindingsMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 55, height - 42, 110, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (mc != null && mc.getSoundHandler() != null && ElementsKrdModMod.sounds.containsKey(CLICK_SOUND)) {
                mc.getSoundHandler().playSound(
                        net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(
                                ElementsKrdModMod.sounds.get(CLICK_SOUND),
                                1.0F
                        )
                );
            }
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int panelWidth = Math.min(300, width - 40);
        int panelHeight = 110;
        int x = (width - panelWidth) / 2;
        int y = (height - panelHeight) / 2 - 20;

        drawRect(x, y, x + panelWidth, y + panelHeight, PANEL_FILL);
        drawRect(x, y, x + panelWidth, y + 1, PANEL_EDGE);
        drawRect(x, y + panelHeight - 1, x + panelWidth, y + panelHeight, PANEL_EDGE);
        drawRect(x, y, x + 1, y + panelHeight, PANEL_EDGE);
        drawRect(x + panelWidth - 1, y, x + panelWidth, y + panelHeight, PANEL_EDGE);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.05F, 1.05F, 1.0F);
        drawCenteredString(fontRenderer, "НАСТРОЙКА СКИЛЛОВ", Math.round(width / 2F / 1.05F), Math.round((y + 16) / 1.05F), TEXT_PRIMARY);
        GlStateManager.popMatrix();

        drawCenteredString(fontRenderer, "Экран-заглушка для будущего меню биндов.", width / 2, y + 44, TEXT_SECONDARY);
        drawCenteredString(fontRenderer, "Позже сюда можно будет вынести клавиши и слоты навыков.", width / 2, y + 60, TEXT_SECONDARY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
