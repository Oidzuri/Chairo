package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BitmapFontRenderer {
    private static class Glyph {
        int x;
        int y;
        int width;
        int height;
        int xOffset;
        int yOffset;
        int xAdvance;
    }

    private final ResourceLocation texture;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final float scale;
    private int lineHeight = 16;
    private int textureWidth = 1024;
    private int textureHeight = 1024;

    public BitmapFontRenderer(String fntPath, String texturePath, float scale) {
        this.texture = new ResourceLocation(texturePath.split(":")[0], texturePath.split(":")[1]);
        this.scale = scale;
        load(fntPath);
    }

    private void load(String fntPath) {
        try {
            ResourceLocation rl = new ResourceLocation(fntPath.split(":")[0], fntPath.split(":")[1]);
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("common ")) {
                        Map<String, String> values = parseLine(line);
                        lineHeight = parseInt(values.get("lineHeight"), 16);
                        textureWidth = parseInt(values.get("scaleW"), 1024);
                        textureHeight = parseInt(values.get("scaleH"), 1024);
                    } else if (line.startsWith("char ")) {
                        Map<String, String> values = parseLine(line);
                        Glyph glyph = new Glyph();
                        int id = parseInt(values.get("id"), -1);
                        if (id < 0) continue;
                        glyph.x = parseInt(values.get("x"), 0);
                        glyph.y = parseInt(values.get("y"), 0);
                        glyph.width = parseInt(values.get("width"), 0);
                        glyph.height = parseInt(values.get("height"), 0);
                        glyph.xOffset = parseInt(values.get("xoffset"), 0);
                        glyph.yOffset = parseInt(values.get("yoffset"), 0);
                        glyph.xAdvance = parseInt(values.get("xadvance"), glyph.width);
                        glyphs.put(id, glyph);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bitmap font: " + fntPath, e);
        }
    }

    private Map<String, String> parseLine(String line) {
        Map<String, String> values = new HashMap<>();
        String[] parts = line.trim().split("\\s+");
        for (String part : parts) {
            int idx = part.indexOf('=');
            if (idx > 0 && idx < part.length() - 1) {
                values.put(part.substring(0, idx), part.substring(idx + 1).replace("\"", ""));
            }
        }
        return values;
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public int getLineHeight() {
        return Math.round(lineHeight * scale);
    }

    public int getStringWidth(String text) {
        if (text == null || text.isEmpty()) return 0;
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            Glyph glyph = glyphs.get((int) text.charAt(i));
            if (glyph != null) {
                width += Math.round(glyph.xAdvance * scale);
            } else {
                width += Minecraft.getMinecraft().fontRenderer.getCharWidth(text.charAt(i));
            }
        }
        return width;
    }

    public void drawString(String text, float x, float y, int color) {
        if (text == null || text.isEmpty()) return;

        FontRenderer vanilla = Minecraft.getMinecraft().fontRenderer;
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        if ((color & 0xFC000000) == 0) alpha = 1.0F;

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        float cursorX = x;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            Glyph glyph = glyphs.get((int) ch);
            if (glyph == null || glyph.width <= 0 || glyph.height <= 0) {
                vanilla.drawString(String.valueOf(ch), (int) cursorX, (int) y, color);
                cursorX += vanilla.getCharWidth(ch);
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
                GlStateManager.color(red, green, blue, alpha);
                continue;
            }

            float drawX = cursorX + glyph.xOffset * scale;
            float drawY = y + glyph.yOffset * scale;
            float drawW = glyph.width * scale;
            float drawH = glyph.height * scale;
            float u0 = glyph.x / (float) textureWidth;
            float v0 = glyph.y / (float) textureHeight;
            float u1 = (glyph.x + glyph.width) / (float) textureWidth;
            float v1 = (glyph.y + glyph.height) / (float) textureHeight;

            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(drawX, drawY + drawH, 0).tex(u0, v1).endVertex();
            buffer.pos(drawX + drawW, drawY + drawH, 0).tex(u1, v1).endVertex();
            buffer.pos(drawX + drawW, drawY, 0).tex(u1, v0).endVertex();
            buffer.pos(drawX, drawY, 0).tex(u0, v0).endVertex();
            tessellator.draw();

            cursorX += glyph.xAdvance * scale;
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }
}
