package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public final class ProfileHotkeyHandler {
    private static final KeyBinding OPEN_PROFILE_KEY =
            new KeyBinding("Открыть профиль KRD", Keyboard.KEY_G, "KRD Mod");

    private ProfileHotkeyHandler() {
    }

    public static void registerKeybinding() {
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(OPEN_PROFILE_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.world == null) {
            return;
        }

        while (OPEN_PROFILE_KEY.isPressed()) {
            GuiScreen current = mc.currentScreen;
            if (current == null) {
                mc.displayGuiScreen(new ProfileMenu(null));
            }
        }
    }
}
