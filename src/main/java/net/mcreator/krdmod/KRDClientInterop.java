package net.mcreator.krdmod;

import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiScreen;

import java.awt.Desktop;
import java.net.URI;

public final class KRDClientInterop {
    public static final String WEBSITE_URL = "https://example.com/";
    public static final String DISCORD_URL = "https://discord.gg/";
    public static final String TELEGRAM_URL = "https://t.me/";

    private KRDClientInterop() {
    }

    public static void openWebsite() {
        openLink(WEBSITE_URL);
    }

    public static void openDiscord() {
        openLink(DISCORD_URL);
    }

    public static void openTelegram() {
        openLink(TELEGRAM_URL);
    }

    public static void openKrdArmCustomization(GuiScreen parent) {
        if (parent == null || parent.mc == null) {
            return;
        }
        try {
            Class<?> screenClass = Class.forName("net.mcreator.krdarm.client.CustomizationScreen");
            Object screen = screenClass.newInstance();
            if (screen instanceof GuiScreen) {
                parent.mc.displayGuiScreen((GuiScreen) screen);
                return;
            }
        } catch (Exception ignored) {
        }
        parent.mc.displayGuiScreen(new GuiCustomizeSkin(parent));
    }

    private static void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ignored) {
        }
    }
}
