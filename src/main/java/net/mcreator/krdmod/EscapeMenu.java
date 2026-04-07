package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.stats.StatList;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public class EscapeMenu extends GuiScreen {

    private long openTime;
    private static boolean soundsEnabled = true;
    private static int particleColor = 0x5500A8FF;
    private static boolean particlesUp = true;
    
    private List<MenuParticle> particles = new ArrayList<>();
    private Random rand = new Random();

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            event.setGui(new EscapeMenu());
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.openTime = System.currentTimeMillis();

        // ? Ã”«€ ¿ (zvuj)
        if (soundsEnabled) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(
                ElementsKrdModMod.sounds.get(new ResourceLocation("krd_mod", "zvuj")), 0.7F, 1.0F));
        }

        // Blur
        if (mc.entityRenderer.getShaderGroup() == null) {
            try {
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            } catch (Exception ignored) {}
        }

        particles.clear();
        for (int i = 0; i < 50; i++) {
            particles.add(new MenuParticle(rand.nextInt(width), rand.nextInt(height)));
        }

        int x = 40;
        int y = 80;

        addBtn(1, x, y, "»„‡Ú¸");
        addBtn(3, x, y + 25, " ÌÓÔÍË");
        addBtn(4, x, y + 50, " ‚ÂÒÚ˚");
        addBtn(9, x, y + 75, " ‡Ú‡");
        addBtn(10, x, y + 100, "ƒÓÌ‡Ú");

        //  ÌÓÔÍË ÛÔ‡‚ÎÂÌËˇ ˜‡ÒÚËˆ‡ÏË
        addBtn(60, 40, height - 105, "—ÏÂÌËÚ¸ ˆ‚ÂÚ");
        addBtn(61, 40, height - 80, "Õ‡Ô‡‚ÎÂÌËÂ: " + (particlesUp ? "¬‚Âı" : "¬ÌËÁ"));

        addBtn(50, 40, height - 55, "«‚ÛÍË UI: " + (soundsEnabled ? "¬ÍÎ" : "¬˚ÍÎ"));
        addBtn(5, 40, height - 30, "¬˚ÈÚË");
        
        buttonList.add(new IconButton(2, 110, height - 30, new ResourceLocation("krd_mod", "textures/ui/icons/settings.png")));

        // —Óˆ. ÒÂÚË
        buttonList.add(new IconButton(6, width - 40, height - 30, new ResourceLocation("krd_mod", "textures/ui/icons/discord.png")));
        buttonList.add(new IconButton(7, width - 65, height - 30, new ResourceLocation("krd_mod", "textures/ui/icons/tg.png")));
        buttonList.add(new IconButton(8, width - 90, height - 30, new ResourceLocation("krd_mod", "textures/ui/icons/web.png")));
    }

    private void addBtn(int id, int x, int y, String text) {
        int w = mc.fontRenderer.getStringWidth(text) + 24;
        buttonList.add(new GuiButtonCustom(id, x, y, w, 20, text));
    }

    @Override
    public void onGuiClosed() {
        mc.entityRenderer.stopUseShader();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        // «‚ÛÍ ÍÎËÍ‡ (ÚÓÎ¸ÍÓ ÂÒÎË Ì‡¯)
        if (soundsEnabled) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(
                ElementsKrdModMod.sounds.get(new ResourceLocation("krd_mod", "click")), 1.0F, 1.0F));
        }

        if (button.id == 1) { mc.displayGuiScreen(null); mc.setIngameFocus(); }
        if (button.id == 2) { mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)); }
        if (button.id == 5) {
            mc.world.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);
            mc.displayGuiScreen(new GuiMainMenu());
        }
        if (button.id == 50) { 
            soundsEnabled = !soundsEnabled;
            button.displayString = "«‚ÛÍË UI: " + (soundsEnabled ? "¬ÍÎ" : "¬˚ÍÎ");
        }
        if (button.id == 60) {
            if (particleColor == 0x5500A8FF) particleColor = 0x55FF4500;
            else if (particleColor == 0x55FF4500) particleColor = 0x5532CD32;
            else if (particleColor == 0x5532CD32) particleColor = 0x559370DB;
            else particleColor = 0x5500A8FF;
        }
        if (button.id == 61) {
            particlesUp = !particlesUp;
            button.displayString = "Õ‡Ô‡‚ÎÂÌËÂ: " + (particlesUp ? "¬‚Âı" : "¬ÌËÁ");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float pt) {
        float fade = Math.min(1f, (System.currentTimeMillis() - openTime) / 400f);
        drawGradientRect(0, 0, width, height, (int)(fade * 160) << 24, (int)(fade * 200) << 24);

        for (MenuParticle p : particles) {
            p.update(width, height);
            drawRect(p.x, p.y, p.x + 1, p.y + 1, particleColor);
        }

        fontRenderer.drawStringWithShadow("KRD PROJECT", 40, 40, 0x00A8FF);
        fontRenderer.drawString("Demon Slayer Server", 40, 55, 0xAAAAAA);

        int panelX = width - 180;
        
        // --- “Œœ »√–Œ Œ¬ (3 ÕŒÃ»Õ¿÷»») ---
        drawRect(panelX, 20, panelX + 160, 115, 0x66000000);
        drawRect(panelX, 20, panelX + 160, 21, 0xAA00A8FF);
        
        fontRenderer.drawStringWithShadow("? “Œœ€ —≈–¬≈–¿", panelX + 10, 30, 0xFFD700);
        fontRenderer.drawString(" ËÎÎ˚: ßfStanislav", panelX + 10, 45, 0x00A8FF);
        fontRenderer.drawString("¬ÂÏˇ: ßfRengoku_22", panelX + 10, 60, 0x00A8FF);
        fontRenderer.drawString("”Ó‚ÂÌ¸: ßfTanjiro", panelX + 10, 75, 0x00A8FF);
        fontRenderer.drawString("ß8----------------", panelX + 10, 88, 0xFFFFFF);
        fontRenderer.drawString("ß7“‚ÓÈ ‡Ì„: ßbŒıÓÚÌËÍ", panelX + 10, 100, 0xFFFFFF);

        // --- “¬Œﬂ —“¿“»—“» ¿ ---
        int statsY = 125;
        drawRect(panelX, statsY, panelX + 160, statsY + 60, 0x66000000);
        drawRect(panelX, statsY, panelX + 160, statsY + 1, 0xAA00A8FF);

        int kills = mc.player.getStatFileWriter().readStat(StatList.MOB_KILLS);
        int deaths = mc.player.getStatFileWriter().readStat(StatList.DEATHS);

        fontRenderer.drawStringWithShadow("? “¬Œ» ƒ¿ÕÕ€≈", panelX + 10, statsY + 10, 0x00A8FF);
        fontRenderer.drawString("”·ËÈÒÚ‚‡: " + kills, panelX + 10, statsY + 25, 0xFFFFFF);
        fontRenderer.drawString("—ÏÂÚË: " + deaths, panelX + 10, statsY + 37, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, pt);
    }

    class MenuParticle {
        int x, y, speed;
        MenuParticle(int x, int y) { this.x = x; this.y = y; this.speed = rand.nextInt(2) + 1; }
        void update(int w, int h) {
            if (particlesUp) y -= speed; else y += speed;
            if (y < 0) { y = h; x = rand.nextInt(w); }
            if (y > h) { y = 0; x = rand.nextInt(w); }
        }
    }

    public static class GuiButtonCustom extends GuiButton {
        private float scale = 1f;
        private boolean wasHovered = false;

        public GuiButtonCustom(int id, int x, int y, int w, int h, String text) { super(id, x, y, w, h, text); }

        @Override
        public void drawButton(Minecraft mc, int mx, int my, float pt) {
            if (!visible) return;
            hovered = mx >= x && my >= y && mx < x + width && my < y + height;

            if (hovered && !wasHovered && soundsEnabled) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(
                    ElementsKrdModMod.sounds.get(new ResourceLocation("krd_mod", "click")), 1.2F, 1.0F));
            }
            wasHovered = hovered;

            float target = hovered ? 1.05f : 1f;
            scale += (target - scale) * 0.2f;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + width / 2f, y + height / 2f, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-(x + width / 2f), -(y + height / 2f), 0);

            drawRect(x, y, x + width, y + height, hovered ? 0xAA00A8FF : 0x66000000);
            drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + (height - 8) / 2, hovered ? 0xFFFFFF : 0xCCCCCC);
            GlStateManager.popMatrix();
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            // ŒÒÚ‡‚ÎˇÂÏ ÔÛÒÚ˚Ï, ˜ÚÓ·˚ Û·‡Ú¸ ÒÚ‡Ì‰‡ÚÌ˚È Á‚ÛÍ ÍÎËÍ‡!
        }
    }

    public static class IconButton extends GuiButton {
        private ResourceLocation icon;
        public IconButton(int id, int x, int y, ResourceLocation icon) { super(id, x, y, 20, 20, ""); this.icon = icon; }
        @Override
        public void drawButton(Minecraft mc, int mx, int my, float pt) {
            if (!visible) return;
            hovered = mx >= x && my >= y && mx < x + width && my < y + height;
            drawRect(x, y, x + width, y + height, hovered ? 0x7700A8FF : 0x44000000);
            mc.getTextureManager().bindTexture(icon);
            drawModalRectWithCustomSizedTexture(x + 2, y + 2, 0, 0, 16, 16, 16, 16);
        }
        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            // œÛÒÚÓ
        }
    }
}