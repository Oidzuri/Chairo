package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public class CustomMainMenu extends GuiScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("krd_mod", "textures/customgui/background.jpg");
    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");
    private static final ResourceLocation HOVER_SOUND = new ResourceLocation("krd_mod", "hover");
    private static final String MAIN_SERVER_NAME = "ChairoLand";
    private static final String MAIN_SERVER_ADDRESS = "26.89.2.177:25565";
    private static final int BACKGROUND_WIDTH = 1920;
    private static final int BACKGROUND_HEIGHT = 1080;

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof net.minecraft.client.gui.GuiMainMenu && !(event.getGui() instanceof CustomMainMenu)) {
            event.setGui(new CustomMainMenu());
        }
    }

    @Override
    public void initGui() {
        buttonList.clear();

        int buttonWidth = Math.min(320, Math.max(212, width / 4));
        int buttonHeight = 38;
        int gap = 14;
        int cardTop = Math.max(30, height / 10);
        int cardWidth = Math.min(420, Math.max(240, width / 3));
        int x = Math.max(18, Math.min(width - buttonWidth - 18, width / 14));
        int startY = Math.max(cardTop + 98, Math.min(height - 188, height / 2 - 24));

        buttonList.add(new MainMenuButton(1, x, startY, buttonWidth, buttonHeight, "Играть"));
        buttonList.add(new MainMenuButton(2, x, startY + (buttonHeight + gap), buttonWidth, buttonHeight, "Одиночная игра"));
        buttonList.add(new MainMenuButton(3, x, startY + (buttonHeight + gap) * 2, buttonWidth, buttonHeight, "Настройки"));
        buttonList.add(new MainMenuButton(5, x, startY + (buttonHeight + gap) * 3, buttonWidth, buttonHeight, "Выйти"));

        int socialY = Math.max(8, height - 34);
        int iconSize = 22;
        int iconGap = 8;
        int siteWidth = Math.max(88, fontRenderer.getStringWidth("Наш сайт!") + 20);
        int footerWidth = siteWidth + iconGap * 2 + iconSize * 2;
        int socialStartX = Math.max(x, width - footerWidth - 20);
        buttonList.add(new WideFooterButton(8, socialStartX, socialY, siteWidth, "Наш сайт!"));
        buttonList.add(new TextIconButton(6, socialStartX + siteWidth + iconGap, socialY - 2, iconSize,
                new ResourceLocation("krd_mod", "textures/ui/ds.png")));
        buttonList.add(new TextIconButton(7, socialStartX + siteWidth + iconGap * 2 + iconSize, socialY - 2, iconSize,
                new ResourceLocation("krd_mod", "textures/ui/tg.png")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            mc.displayGuiScreen(new GuiConnecting(this, mc, new ServerData(MAIN_SERVER_NAME, MAIN_SERVER_ADDRESS, false)));
        } else if (button.id == 2) {
            mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (button.id == 3) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        } else if (button.id == 5) {
            mc.shutdown();
        } else if (button.id == 6) {
            KRDClientInterop.openDiscord();
        } else if (button.id == 7) {
            KRDClientInterop.openTelegram();
        } else if (button.id == 8) {
            KRDClientInterop.openWebsite();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackgroundImage();
        drawGradientRect(0, 0, width, height, 0x34040B12, 0x9602060A);
        drawGradientRect(0, 0, width, height, 0x0800DFFF, 0x00000000);

        int left = Math.max(18, Math.min(width - 250, width / 14));
        int top = Math.max(30, height / 10);
        int cardWidth = Math.min(452, Math.max(270, width / 3));
        int cardHeight = Math.min(132, Math.max(104, height / 5));
        drawRect(left - 16, top - 14, left + cardWidth, top + cardHeight, 0x56060D14);
        drawRect(left - 16, top - 14, left + cardWidth, top - 13, 0x6929E3FF);
        drawRect(left - 16, top + cardHeight - 1, left + cardWidth, top + cardHeight, 0x180E1B22);

        GlStateManager.pushMatrix();
        float titleScale = width < 1100 ? 1.55F : 1.9F;
        GlStateManager.scale(titleScale, titleScale, 1.0F);
        drawString(fontRenderer, "KRD MOD", Math.round(left / titleScale), Math.round(top / titleScale), 0xF2FCFF);
        GlStateManager.popMatrix();

        drawString(fontRenderer, "Дыхание. Демоны. История.", left, top + 34, 0x98E4F4);
        drawString(fontRenderer, "Кастомное меню клиента", left, top + 50, 0xAFC5D1);
        drawString(fontRenderer, MAIN_SERVER_NAME, left, top + 66, 0xC7DDE6);

        int footerY = height - 16;
        drawString(fontRenderer, "Forge 1.12.2", left, footerY, 0x8FA6B2);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBackgroundImage() {
        mc.getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color(1F, 1F, 1F, 1F);
        float scale = Math.max((float) width / BACKGROUND_WIDTH, (float) height / BACKGROUND_HEIGHT);
        int drawWidth = Math.round(BACKGROUND_WIDTH * scale);
        int drawHeight = Math.round(BACKGROUND_HEIGHT * scale);
        int drawX = (width - drawWidth) / 2;
        int drawY = (height - drawHeight) / 2;
        GuiScreen.drawModalRectWithCustomSizedTexture(drawX, drawY, 0, 0, drawWidth, drawHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    private static void playUiSound(ResourceLocation soundId, float volume) {
        if (!ElementsKrdModMod.sounds.containsKey(soundId)) {
            return;
        }
        Minecraft.getMinecraft().getSoundHandler().playSound(
                PositionedSoundRecord.getMasterRecord(ElementsKrdModMod.sounds.get(soundId), volume)
        );
    }

    private static class MainMenuButton extends GuiButton {
        private boolean lastHovered;
        private float hoverAnim;

        MainMenuButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }

            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            if (hovered && !lastHovered) {
                playUiSound(HOVER_SOUND, 0.35F);
            }
            lastHovered = hovered;
            float target = hovered ? 1.0F : 0.0F;
            hoverAnim += (target - hoverAnim) * 0.28F;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            GlStateManager.disableTexture2D();

            int fill = mixColor(0xB8F2F2F2, 0xCCFFFFFF, hoverAnim);
            int edge = mixColor(0x44FFFFFF, 0x8A3CE7FF, hoverAnim);
            drawSkewPanel(x, y, width, height, fill);
            drawPanelOutline(x, y, width, height, edge);

            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();

            int textColor = hovered ? 0x51E6FF : 0x61707A;
            drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + (height - 8) / 2, textColor);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.5F);
        }

        private static void drawSkewPanel(int x, int y, int width, int height, int color) {
            int skew = Math.max(12, height / 2);
            Tess.start(GL11.GL_QUADS, color);
            Tess.vertex(x + skew, y);
            Tess.vertex(x + width, y);
            Tess.vertex(x + width - skew, y + height);
            Tess.vertex(x, y + height);
            Tess.draw();
        }

        private static void drawPanelOutline(int x, int y, int width, int height, int color) {
            int skew = Math.max(12, height / 2);
            Tess.start(GL11.GL_LINE_LOOP, color);
            Tess.vertex(x + skew, y);
            Tess.vertex(x + width, y);
            Tess.vertex(x + width - skew, y + height);
            Tess.vertex(x, y + height);
            Tess.draw();
        }

        private static int mixColor(int from, int to, float factor) {
            factor = Math.max(0.0F, Math.min(1.0F, factor));
            int a = mix((from >>> 24) & 255, (to >>> 24) & 255, factor);
            int r = mix((from >>> 16) & 255, (to >>> 16) & 255, factor);
            int g = mix((from >>> 8) & 255, (to >>> 8) & 255, factor);
            int b = mix(from & 255, to & 255, factor);
            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        private static int mix(int from, int to, float factor) {
            return Math.round(from + (to - from) * factor);
        }
    }

    private static class TextIconButton extends GuiButton {
        private final ResourceLocation icon;

        TextIconButton(int buttonId, int x, int y, int size, ResourceLocation icon) {
            super(buttonId, x, y, size, size, "");
            this.icon = icon;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawRect(x, y, x + width, y + height, hovered ? 0x36FFFFFF : 0x16000000);
            mc.getTextureManager().bindTexture(icon);
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawModalRectWithCustomSizedTexture(x + 2, y + 2, 0, 0, width - 4, height - 4, 16, 16);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.45F);
        }
    }

    private static class WideFooterButton extends GuiButton {
        WideFooterButton(int buttonId, int x, int y, int width, String text) {
            super(buttonId, x, y, width, 16, text);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawRect(x, y, x + width, y + height, hovered ? 0x2EFFFFFF : 0x12000000);
            int color = hovered ? 0xEAF7FF : 0x9CBAC7;
            drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + 4, color);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.45F);
        }
    }

    private static class Tess {
        private static net.minecraft.client.renderer.Tessellator tessellator;
        private static net.minecraft.client.renderer.BufferBuilder buffer;

        static void start(int mode, int color) {
            tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
            buffer = tessellator.getBuffer();
            float a = ((color >>> 24) & 255) / 255.0F;
            float r = ((color >>> 16) & 255) / 255.0F;
            float g = ((color >>> 8) & 255) / 255.0F;
            float b = (color & 255) / 255.0F;
            buffer.begin(mode, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR);
            currentR = r;
            currentG = g;
            currentB = b;
            currentA = a;
        }

        private static float currentR;
        private static float currentG;
        private static float currentB;
        private static float currentA;

        static void vertex(double x, double y) {
            buffer.pos(x, y, 0).color(currentR, currentG, currentB, currentA).endVertex();
        }

        static void draw() {
            tessellator.draw();
        }
    }
}
