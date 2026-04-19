package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BitmapFontRenderer {
    private final ResourceLocation fontData;
    private final ResourceLocation fontTexture;
    private final float scale;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();

    private boolean loaded;
    private int lineHeight = 12;
    private int textureWidth = 256;
    private int textureHeight = 256;
    private int fallbackAdvance = 8;

    public BitmapFontRenderer(String fontDataPath, String fontTexturePath, float scale) {
        this.fontData = parseLocation(fontDataPath);
        this.fontTexture = parseLocation(fontTexturePath);
        this.scale = scale <= 0 ? 1.0F : scale;
    }

    public int getStringWidth(String text) {
        ensureLoaded();
        if (text == null || text.isEmpty()) {
            return 0;
        }

        Minecraft mc = Minecraft.getMinecraft();
        float width = 0.0F;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                break;
            }
            Glyph glyph = glyphs.get((int) ch);
            if (glyph != null) {
                width += glyph.xAdvance;
            } else if (mc != null && mc.fontRenderer != null) {
                width += mc.fontRenderer.getStringWidth(String.valueOf(ch)) / scale;
            } else {
                width += fallbackAdvance;
            }
        }
        return Math.max(0, Math.round(width * scale));
    }

    public int getLineHeight() {
        ensureLoaded();
        return Math.max(1, Math.round(lineHeight * scale));
    }

    public void drawString(String text, float x, float y, int color) {
        ensureLoaded();
        if (text == null || text.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getTextureManager() == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        int alpha = (color >>> 24) & 255;
        if (alpha == 0) {
            alpha = 255;
        }
        GlStateManager.color(
                ((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F,
                alpha / 255.0F
        );
        GlStateManager.scale(scale, scale, 1.0F);
        mc.getTextureManager().bindTexture(fontTexture);

        float drawX = x / scale;
        float drawY = y / scale;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                drawY += lineHeight;
                drawX = x / scale;
                continue;
            }

            Glyph glyph = glyphs.get((int) ch);
            if (glyph == null) {
                if (mc.fontRenderer != null) {
                    GlStateManager.popMatrix();
                    mc.fontRenderer.drawString(String.valueOf(ch), Math.round(drawX * scale), Math.round(drawY * scale), color, false);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.tryBlendFuncSeparate(
                            GlStateManager.SourceFactor.SRC_ALPHA,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO
                    );
                    GlStateManager.color(
                            ((color >> 16) & 255) / 255.0F,
                            ((color >> 8) & 255) / 255.0F,
                            (color & 255) / 255.0F,
                            alpha / 255.0F
                    );
                    GlStateManager.scale(scale, scale, 1.0F);
                    mc.getTextureManager().bindTexture(fontTexture);
                    drawX += mc.fontRenderer.getStringWidth(String.valueOf(ch)) / scale;
                } else {
                    drawX += fallbackAdvance;
                }
                continue;
            }

            if (glyph.width > 0 && glyph.height > 0) {
                Gui.drawModalRectWithCustomSizedTexture(
                        Math.round(drawX + glyph.xOffset),
                        Math.round(drawY + glyph.yOffset),
                        glyph.x,
                        glyph.y,
                        glyph.width,
                        glyph.height,
                        textureWidth,
                        textureHeight
                );
            }
            drawX += glyph.xAdvance;
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void ensureLoaded() {
        if (loaded) {
            return;
        }

        loaded = true;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getResourceManager() == null) {
            return;
        }

        try (IResource resource = mc.getResourceManager().getResource(fontData);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("common ")) {
                    lineHeight = parseIntValue(line, "lineHeight", lineHeight);
                    textureWidth = parseIntValue(line, "scaleW", textureWidth);
                    textureHeight = parseIntValue(line, "scaleH", textureHeight);
                } else if (line.startsWith("char ")) {
                    int id = parseIntValue(line, "id", -1);
                    if (id < 0) {
                        continue;
                    }

                    Glyph glyph = new Glyph();
                    glyph.x = parseIntValue(line, "x", 0);
                    glyph.y = parseIntValue(line, "y", 0);
                    glyph.width = parseIntValue(line, "width", 0);
                    glyph.height = parseIntValue(line, "height", 0);
                    glyph.xOffset = parseIntValue(line, "xoffset", 0);
                    glyph.yOffset = parseIntValue(line, "yoffset", 0);
                    glyph.xAdvance = parseIntValue(line, "xadvance", fallbackAdvance);
                    fallbackAdvance = Math.max(1, glyph.xAdvance);
                    glyphs.put(id, glyph);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static int parseIntValue(String line, String key, int fallback) {
        int index = line.indexOf(key + "=");
        if (index < 0) {
            return fallback;
        }

        int start = index + key.length() + 1;
        int end = start;
        while (end < line.length()) {
            char current = line.charAt(end);
            if ((current < '0' || current > '9') && current != '-') {
                break;
            }
            end++;
        }

        try {
            return Integer.parseInt(line.substring(start, end));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static ResourceLocation parseLocation(String path) {
        String[] split = path.split(":", 2);
        return split.length == 2 ? new ResourceLocation(split[0], split[1]) : new ResourceLocation(path);
    }

    private static class Glyph {
        int x;
        int y;
        int width;
        int height;
        int xOffset;
        int yOffset;
        int xAdvance;
    }
}
