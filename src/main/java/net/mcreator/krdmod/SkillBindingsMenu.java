package net.mcreator.krdmod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class SkillBindingsMenu extends GuiScreen {
    private final GuiScreen parent;

    public SkillBindingsMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 60, height - 40, 120, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Настройка скиллов", width / 2, 34, 0xEAFBFF);
        drawCenteredString(fontRenderer, "Здесь будет меню биндов и управления умениями.", width / 2, 58, 0xBFD7E2);
        drawCenteredString(fontRenderer, "Пока это отдельный экран-заглушка под будущую настройку.", width / 2, 72, 0x94B5C3);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
