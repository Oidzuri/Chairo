package net.mcreator.krdmod;

import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = "krdmod", value = Side.CLIENT)
public class MenuHandler {
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        // Если игра пытается открыть ванильное меню паузы (на Esc)
        if (event.getGui() instanceof GuiIngameMenu) {
            // Подменяем его на наше кастомное меню
            event.setGui(new EscapeMenu());
        }
    }
}