package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public final class KRDGuiScaleManager {
    private static final int MENU_GUI_SCALE = 2;
    private static Integer previousGuiScale;

    private KRDGuiScaleManager() {
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) {
            return;
        }

        GuiScreen nextGui = event.getGui();
        boolean nextManaged = isManagedScreen(nextGui);
        boolean currentManaged = isManagedScreen(mc.currentScreen);

        if (nextManaged) {
            if (previousGuiScale == null) {
                previousGuiScale = mc.gameSettings.guiScale;
            }
            if (mc.gameSettings.guiScale != MENU_GUI_SCALE) {
                mc.gameSettings.guiScale = MENU_GUI_SCALE;
            }
            return;
        }

        if (previousGuiScale != null && currentManaged) {
            mc.gameSettings.guiScale = previousGuiScale;
            previousGuiScale = null;
        }
    }

    private static boolean isManagedScreen(GuiScreen screen) {
        return screen instanceof CustomMainMenu
                || screen instanceof EscapeMenu
                || screen instanceof ProfileMenu
                || screen instanceof MyBreathingsMenu
                || screen instanceof BreathSkillsMenu
                || screen instanceof SkillBindingsMenu
                || screen instanceof LevelUpgradeMenu
                || screen instanceof StatsUpgradeMenu
                || screen instanceof KRDChatSettingsScreen;
    }
}
