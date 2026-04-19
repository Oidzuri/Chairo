package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class KRDChatSettings {
    private static final String CONFIG_FILE_NAME = "config/krd_chat_overlay.properties";

    private static boolean loaded;
    private static long lastModified = -1L;
    private static float uiScale = 1.0F;
    private static float widthScale = 1.0F;
    private static float heightScale = 1.0F;
    private static float compactOpacity = 0.56F;
    private static float expandedOpacity = 0.75F;

    private KRDChatSettings() {
    }

    public static void ensureLoaded(Minecraft mc) {
        File configFile = getConfigFile(mc);
        long modified = configFile.exists() ? configFile.lastModified() : -1L;
        if (loaded && modified == lastModified) {
            return;
        }

        Properties properties = new Properties();
        if (configFile.exists()) {
            try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(configFile))) {
                properties.load(input);
            } catch (IOException ignored) {
            }
        }

        uiScale = clamp(readFloat(properties, "ui_scale", 1.0F), 0.75F, 1.75F);
        widthScale = clamp(readFloat(properties, "width_scale", 1.0F), 0.65F, 1.75F);
        heightScale = clamp(readFloat(properties, "height_scale", 1.0F), 0.70F, 1.75F);
        compactOpacity = clamp(readFloat(properties, "compact_opacity", 0.56F), 0.15F, 1.0F);
        expandedOpacity = clamp(readFloat(properties, "expanded_opacity", 0.75F), 0.15F, 1.0F);

        if (!configFile.exists()) {
            save(mc);
            modified = configFile.lastModified();
        }

        loaded = true;
        lastModified = modified;
    }

    public static void save(Minecraft mc) {
        if (mc == null) {
            return;
        }

        File configFile = getConfigFile(mc);
        File parent = configFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Properties properties = new Properties();
        properties.setProperty("ui_scale", format(uiScale));
        properties.setProperty("width_scale", format(widthScale));
        properties.setProperty("height_scale", format(heightScale));
        properties.setProperty("compact_opacity", format(compactOpacity));
        properties.setProperty("expanded_opacity", format(expandedOpacity));

        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(configFile))) {
            properties.store(output, "KRD chat overlay settings");
        } catch (IOException ignored) {
        }

        loaded = true;
        lastModified = configFile.exists() ? configFile.lastModified() : -1L;
    }

    public static float getUiScale(Minecraft mc) {
        ensureLoaded(mc);
        return uiScale;
    }

    public static float getWidthScale(Minecraft mc) {
        ensureLoaded(mc);
        return widthScale;
    }

    public static float getHeightScale(Minecraft mc) {
        ensureLoaded(mc);
        return heightScale;
    }

    public static float getCompactOpacity(Minecraft mc) {
        ensureLoaded(mc);
        return compactOpacity;
    }

    public static float getExpandedOpacity(Minecraft mc) {
        ensureLoaded(mc);
        return expandedOpacity;
    }

    public static void setUiScale(float value) {
        uiScale = clamp(value, 0.75F, 1.75F);
        loaded = true;
    }

    public static void setWidthScale(float value) {
        widthScale = clamp(value, 0.65F, 1.75F);
        loaded = true;
    }

    public static void setHeightScale(float value) {
        heightScale = clamp(value, 0.70F, 1.75F);
        loaded = true;
    }

    public static void setCompactOpacity(float value) {
        compactOpacity = clamp(value, 0.15F, 1.0F);
        loaded = true;
    }

    public static void setExpandedOpacity(float value) {
        expandedOpacity = clamp(value, 0.15F, 1.0F);
        loaded = true;
    }

    private static File getConfigFile(Minecraft mc) {
        return new File(mc.mcDataDir, CONFIG_FILE_NAME);
    }

    private static float readFloat(Properties properties, String key, float fallback) {
        String value = properties.getProperty(key, Float.toString(fallback));
        try {
            return Float.parseFloat(value.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static String format(float value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
