package net.mcreator.krdmod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public final class KRDOptionsHooks {
    private static final int PROFILE_BUTTON_ID = 932451;

    private KRDOptionsHooks() {
    }

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiOptions)) {
            return;
        }
        int x = event.getGui().width / 2 - 155;
        int y = event.getGui().height / 6 + 144;
        event.getButtonList().add(new GuiButton(PROFILE_BUTTON_ID, x, y, 150, 20, "Профиль KRD"));
        event.getButtonList().add(new GuiButton(PROFILE_BUTTON_ID + 1, x + 160, y, 150, 20, "Персонализация"));
    }

    @SubscribeEvent
    public static void onAction(GuiScreenEvent.ActionPerformedEvent.Post event) throws IOException {
        if (!(event.getGui() instanceof GuiOptions)) {
            return;
        }
        if (event.getButton().id == PROFILE_BUTTON_ID) {
            event.getGui().mc.displayGuiScreen(new ProfileMenu(event.getGui()));
        } else if (event.getButton().id == PROFILE_BUTTON_ID + 1) {
            openKrdArmCustomization(event.getGui());
        }
    }

    private static void openKrdArmCustomization(net.minecraft.client.gui.GuiScreen parent) {
        KRDClientInterop.openKrdArmCustomization(parent);
    }
}
