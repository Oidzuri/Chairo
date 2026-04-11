package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public class EscapeMenu extends GuiScreen {
    private static final int PANEL_FILL = 0x70060B12;
    private static final int PANEL_EDGE = 0x1A86DFFF;
    private static final int PANEL_SOFT = 0x10000000;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int CYAN_ACCENT = 0x63E6FF;
    private static final BitmapFontRenderer MENU_FONT = new BitmapFontRenderer(
            "minecraft:textures/font/jetbrains_mono.fnt",
            "minecraft:textures/font/jetbrains_mono.png",
            0.096f
    );

    private static final ResourceLocation CLICK_SOUND = new ResourceLocation("krd_mod", "click");
    private static final ResourceLocation HOVER_SOUND = new ResourceLocation("krd_mod", "hover");
    private static final int LIST_LINE_HEIGHT = 12;
    private static final int LEFT_MENU_MIN_WIDTH = 106;
    private static final int LEFT_MENU_MAX_WIDTH = 172;
    private static final int BUTTON_HEIGHT = 18;
    private static final int BUTTON_SPACING = 22;

    private static final int[] COLORS = {0x5500A8FF, 0x55FF0055, 0x5500FF55, 0x55FFDD00, 0x55AA00FF, 0x55FFFFFF};
    private static final String[] COLOR_NAMES = {"Голубой", "Малиновый", "Изумруд", "Золотой", "Фиолетовый", "Белый"};
    private static final int[][] DIRS = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {1, 1}};
    private static final String[] DIR_NAMES = {"Вверх", "Вниз", "Влево", "Вправо", "Диагональ", "Искры"};
    private static boolean soundsEnabled = true;
    private static int colorIdx = 0;
    private static int dirIdx = 0;
    private static boolean settingsExpanded = false;
    public static int currentTop = 0;
    private static int sessionKills = 0;
    private static int sessionDeaths = 0;
    private static int sessionJumps = 0;
    private static int sessionChestOpens = 0;
    private static int sessionVillagerTalks = 0;
    private static int sessionSleepCount = 0;
    private static float sessionDamageDealt = 0F;
    private static float sessionDamageTaken = 0F;
    private static double sessionWalkCm = 0D;
    private static double sessionBoatCm = 0D;
    private static double sessionHorseCm = 0D;
    private static double sessionDiveCm = 0D;
    private static int sessionPlayTicks = 0;
    private static boolean lastOnGround = true;
    private static boolean lastSleeping = false;
    private static boolean chestScreenOpen = false;
    private static boolean merchantScreenOpen = false;
    private static boolean hasLastPosition = false;
    private static double lastPlayerX = 0D;
    private static double lastPlayerY = 0D;
    private static double lastPlayerZ = 0D;

    private final List<MenuParticle> particles = new ArrayList<>();
    private final Random rand = new Random();

    private long openTime;
    private long lastStatsRefresh;

    private UIScrollList topScroll;
    private UIScrollList statsScroll;
    private int leftMenuX;
    private int actionsLabelY;
    private int settingsLabelY;
    private UILeftButton settingsToggleButton;
    private UILeftButton colorButton;
    private UILeftButton directionButton;
    private UILeftButton soundButton;
    private UILeftButton exitButton;
    private IconButton optionsIconButton;

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            event.setGui(new EscapeMenu());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) {
            hasLastPosition = false;
            chestScreenOpen = false;
            merchantScreenOpen = false;
            return;
        }

        sessionPlayTicks++;

        if (!hasLastPosition) {
            lastPlayerX = mc.player.posX;
            lastPlayerY = mc.player.posY;
            lastPlayerZ = mc.player.posZ;
            hasLastPosition = true;
        } else {
            double dx = mc.player.posX - lastPlayerX;
            double dy = mc.player.posY - lastPlayerY;
            double dz = mc.player.posZ - lastPlayerZ;
            double movedCm = Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0D;
            if (movedCm > 0.0D && movedCm < 1200.0D) {
                if (mc.player.isInWater()) {
                    sessionDiveCm += movedCm;
                } else if (mc.player.isRidingHorse()) {
                    sessionHorseCm += movedCm;
                } else if (mc.player.getRidingEntity() instanceof net.minecraft.entity.item.EntityBoat) {
                    sessionBoatCm += movedCm;
                } else {
                    sessionWalkCm += movedCm;
                }
            }
            lastPlayerX = mc.player.posX;
            lastPlayerY = mc.player.posY;
            lastPlayerZ = mc.player.posZ;
        }

        if (lastOnGround && !mc.player.onGround && mc.player.motionY > 0.0D) {
            sessionJumps++;
        }
        lastOnGround = mc.player.onGround;

        if (mc.player.isPlayerSleeping() && !lastSleeping) {
            sessionSleepCount++;
        }
        lastSleeping = mc.player.isPlayerSleeping();

        boolean chestOpenNow = mc.currentScreen instanceof GuiChest;
        if (chestOpenNow && !chestScreenOpen) {
            sessionChestOpens++;
        }
        chestScreenOpen = chestOpenNow;

        boolean merchantOpenNow = mc.currentScreen instanceof GuiMerchant;
        if (merchantOpenNow && !merchantScreenOpen) {
            sessionVillagerTalks++;
        }
        merchantScreenOpen = merchantOpenNow;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || !mc.world.isRemote) {
            return;
        }

        if (event.getEntity() == mc.player) {
            sessionDeaths++;
        }
        if (event.getSource() != null && event.getSource().getTrueSource() == mc.player && event.getEntity() != mc.player) {
            sessionKills++;
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || !mc.world.isRemote) {
            return;
        }

        if (event.getEntity() == mc.player) {
            sessionDamageTaken += event.getAmount();
        }
        if (event.getSource() != null && event.getSource().getTrueSource() == mc.player && event.getEntity() != mc.player) {
            sessionDamageDealt += event.getAmount();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        openTime = System.currentTimeMillis();
        lastStatsRefresh = 0L;

        if (mc.entityRenderer.getShaderGroup() == null) {
            try {
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            } catch (Exception ignored) {
            }
        }

        rebuildParticles();
        rebuildLayout();
        updateTopData();
        updateStatsData();
    }

    private void rebuildParticles() {
        particles.clear();
        int count = Math.max(28, Math.min(54, (width * height) / 28000));
        for (int i = 0; i < count; i++) {
            particles.add(new MenuParticle(rand.nextInt(Math.max(1, width)), rand.nextInt(Math.max(1, height))));
        }
    }

    private void rebuildLayout() {
        leftMenuX = Math.max(12, width / 72);
        int topButtonY = Math.max(92, height / 6);
        int mainGroupHeight = BUTTON_SPACING * 4;
        int bottomButtonsY = Math.max(topButtonY + mainGroupHeight + 38, height - 122);
        bottomButtonsY = Math.min(bottomButtonsY, height - 94);
        actionsLabelY = topButtonY - 16;
        settingsLabelY = bottomButtonsY - 16;

        int y = topButtonY;
        addAutoBtn(1, leftMenuX, y, "Вернуться в игру");
        addAutoBtn(3, leftMenuX, y += BUTTON_SPACING, "Настройка скиллов");
        addAutoBtn(4, leftMenuX, y += BUTTON_SPACING, "Мои задания");
        addAutoBtn(9, leftMenuX, y += BUTTON_SPACING, "Карта мира");
        addAutoBtn(10, leftMenuX, y += BUTTON_SPACING, "Магазин дыханий");

        settingsToggleButton = addAutoBtn(62, leftMenuX, bottomButtonsY, settingsExpanded ? "Настройки: скрыть" : "Настройки: показать");
        colorButton = addAutoBtn(60, leftMenuX, bottomButtonsY + BUTTON_SPACING, "Цвет: " + COLOR_NAMES[colorIdx]);
        directionButton = addAutoBtn(61, leftMenuX, bottomButtonsY + BUTTON_SPACING * 2, "Направление: " + DIR_NAMES[dirIdx]);
        soundButton = addAutoBtn(50, leftMenuX, bottomButtonsY + BUTTON_SPACING * 3, soundsEnabled ? "Звук: Вкл" : "Звук: Выкл");

        colorButton.visible = settingsExpanded;
        directionButton.visible = settingsExpanded;
        soundButton.visible = settingsExpanded;

        int exitY = height - 24;
        exitButton = addAutoBtn(5, leftMenuX, exitY, "Выйти из игры");
        optionsIconButton = new IconButton(2, exitButton.x + exitButton.width + 6, exitButton.y,
                new ResourceLocation("krd_mod", "textures/ui/logo.png"));
        buttonList.add(optionsIconButton);

        int panelW = Math.min(224, Math.max(196, width / 5));
        int panelX = width - panelW - Math.max(14, width / 70);
        int topPanelY = 20;
        int topPanelH = Math.max(146, height / 3 - 4);
        int bottomPanelY = topPanelY + topPanelH + 10;
        int bottomPanelH = height - bottomPanelY - 20;

        int topBtnW = panelW / 3;
        buttonList.add(new UITopButton(100, panelX, topPanelY, topBtnW, 22, "Киллы", 0));
        buttonList.add(new UITopButton(101, panelX + topBtnW, topPanelY, topBtnW, 22, "Время", 1));
        buttonList.add(new UITopButton(102, panelX + topBtnW * 2, topPanelY, panelW - topBtnW * 2, 22, "Лвл", 2));

        topScroll = new UIScrollList(panelX + 8, topPanelY + 34, panelW - 16, topPanelH - 46);
        statsScroll = new UIScrollList(panelX + 8, bottomPanelY + 36, panelW - 16, bottomPanelH - 68);

        int socialY = bottomPanelY + bottomPanelH - 26;
        int socialStartX = panelX + panelW - 66;
        buttonList.add(new TextIconButton(8, socialStartX, socialY, "WEB"));
        buttonList.add(new TextIconButton(6, socialStartX + 22, socialY, "DS"));
        buttonList.add(new TextIconButton(7, socialStartX + 44, socialY, "TG"));
    }

    private UILeftButton addAutoBtn(int id, int x, int y, String text) {
        int width = Math.min(LEFT_MENU_MAX_WIDTH, Math.max(LEFT_MENU_MIN_WIDTH, MENU_FONT.getStringWidth(text) + 20));
        UILeftButton btn = new UILeftButton(id, x, y, width, BUTTON_HEIGHT, text);
        buttonList.add(btn);
        return btn;
    }

    private void updateSettingsSectionVisibility() {
        if (settingsToggleButton != null) {
            settingsToggleButton.displayString = settingsExpanded ? "Настройки: скрыть" : "Настройки: показать";
        }
        if (colorButton != null) {
            colorButton.visible = settingsExpanded;
        }
        if (directionButton != null) {
            directionButton.visible = settingsExpanded;
        }
        if (soundButton != null) {
            soundButton.visible = settingsExpanded;
        }
    }

    private int getStatOrSession(net.minecraft.stats.StatBase stat, int sessionValue) {
        if (mc == null || mc.player == null) {
            return sessionValue;
        }
        try {
            return Math.max(mc.player.getStatFileWriter().readStat(stat), sessionValue);
        } catch (Exception ignored) {
            return sessionValue;
        }
    }

    private int getMobKills() {
        return getStatOrSession(StatList.MOB_KILLS, sessionKills);
    }

    private int getDeaths() {
        return getStatOrSession(StatList.DEATHS, sessionDeaths);
    }

    private int getJumps() {
        return getStatOrSession(StatList.JUMP, sessionJumps);
    }

    private int getChestOpens() {
        return getStatOrSession(StatList.CHEST_OPENED, sessionChestOpens);
    }

    private int getVillagerTalks() {
        return getStatOrSession(StatList.TALKED_TO_VILLAGER, sessionVillagerTalks);
    }

    private int getPlayTicks() {
        return getStatOrSession(StatList.PLAY_ONE_MINUTE, sessionPlayTicks);
    }

    private void updateTopData() {
        List<String> data = new ArrayList<>();
        if (mc.player == null) {
            data.add("Загрузка данных...");
            topScroll.setData(data);
            return;
        }

        if (currentTop == 0) {
            data.add(mc.player.getName() + " - " + getMobKills() + " киллов");
            data.add("Твоя серия станет выше, если держать темп боя и не терять позицию.");
        } else if (currentTop == 1) {
            int minutes = getPlayTicks() / 1200;
            data.add(mc.player.getName() + " - " + formatMinutes(minutes));
            data.add("Время в мире тоже ресурс: его лучше тратить на путь, а не на возвраты.");
        } else {
            data.add(mc.player.getName() + " - " + mc.player.experienceLevel + " ур.");
            data.add("Чем выше уровень, тем важнее не только урон, но и ритм снаряжения.");
        }

        topScroll.setData(data);
    }

    private void updateStatsData() {
        List<String> stats = new ArrayList<>();
        if (mc.player != null) {
            stats.add("Убийств: " + getMobKills());
            stats.add("Смертей: " + getDeaths());
            stats.add("Прыжков: " + getJumps());
            stats.add("Сундуков: " + getChestOpens());
            stats.add("Разговоров: " + getVillagerTalks());
            stats.add("Дистанция: " + formatDistance((int) Math.max(getStatOrSession(StatList.WALK_ONE_CM, 0), Math.round(sessionWalkCm))));
            stats.add("На лодке: " + formatDistance((int) Math.max(getStatOrSession(StatList.BOAT_ONE_CM, 0), Math.round(sessionBoatCm))));
            stats.add("На лошади: " + formatDistance((int) Math.max(getStatOrSession(StatList.HORSE_ONE_CM, 0), Math.round(sessionHorseCm))));
            stats.add("Под водой: " + formatDistance((int) Math.max(getStatOrSession(StatList.DIVE_ONE_CM, 0), Math.round(sessionDiveCm))));
            stats.add("Поймано рыбы: " + getStatOrSession(StatList.FISH_CAUGHT, 0));
            stats.add("Предметов выброшено: " + getStatOrSession(StatList.DROP, 0));
            stats.add("Урона нанесено: " + Math.max(getStatOrSession(StatList.DAMAGE_DEALT, 0), Math.round(sessionDamageDealt)));
            stats.add("Урона получено: " + Math.max(getStatOrSession(StatList.DAMAGE_TAKEN, 0), Math.round(sessionDamageTaken)));
            stats.add("Проведено времени: " + formatMinutes(getPlayTicks() / 1200));
            stats.add("Сон в кровати: " + Math.max(getStatOrSession(StatList.SLEEP_IN_BED, 0), sessionSleepCount));
        }
        statsScroll.setData(stats);
    }

    private String formatDistance(int centimeters) {
        int meters = centimeters / 100;
        if (meters >= 1000) {
            return String.format("%.1f км", meters / 1000.0);
        }
        return meters + " м";
    }

    private String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return hours + "ч " + mins + "м";
        }
        return mins + " мин";
    }

    private static void playUiSound(ResourceLocation soundId, float volume, float pitch) {
        if (!soundsEnabled || !ElementsKrdModMod.sounds.containsKey(soundId)) {
            return;
        }
        Minecraft.getMinecraft().getSoundHandler().playSound(
                PositionedSoundRecord.getMasterRecord(ElementsKrdModMod.sounds.get(soundId), volume * pitch)
        );
    }

    @Override
    public void onGuiClosed() {
        mc.entityRenderer.stopUseShader();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        } else if (button.id == 2) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        } else if (button.id == 5) {
            if (mc.world != null) {
                mc.world.sendQuittingDisconnectingPacket();
            }
            mc.loadWorld(null);
            mc.displayGuiScreen(new GuiMainMenu());
        } else if (button.id >= 100 && button.id <= 102) {
            currentTop = button.id - 100;
            updateTopData();
        } else if (button.id == 60) {
            colorIdx = (colorIdx + 1) % COLORS.length;
            button.displayString = "Цвет: " + COLOR_NAMES[colorIdx];
        } else if (button.id == 61) {
            dirIdx = (dirIdx + 1) % DIRS.length;
            button.displayString = "Направление: " + DIR_NAMES[dirIdx];
        } else if (button.id == 62) {
            settingsExpanded = !settingsExpanded;
            updateSettingsSectionVisibility();
        } else if (button.id == 50) {
            soundsEnabled = !soundsEnabled;
            button.displayString = soundsEnabled ? "Звук: Вкл" : "Звук: Выкл";
        } else if (button.id == 6) {
            openLink("https://discord.gg/");
        } else if (button.id == 7) {
            openLink("https://t.me/");
        } else if (button.id == 8) {
            openLink("https://example.com/");
        } else if (button.id == 3) {
            mc.displayGuiScreen(new SkillBindingsMenu(this));
        } else if (button.id == 4) {
            openQuestJournal();
        } else if (button.id == 9) {
            openJourneyMap();
        } else if (button.id == 10) {
            openLink("https://example.com/donate");
        }
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ignored) {
        }
    }

    private void openQuestJournal() {
        mc.displayGuiScreen(null);
        mc.setIngameFocus();
        if (!pressKeybind("quest") && !pressKeybind("noppes") && !pressKeybind("customnpcs")) {
            if (mc.player != null) {
                mc.player.sendChatMessage("/quests");
            }
        }
    }

    private void openJourneyMap() {
        mc.displayGuiScreen(null);
        mc.setIngameFocus();
        if (!pressKeybind("journey") && !pressKeybind("map")) {
            if (mc.player != null) {
                mc.player.sendChatMessage("/journeymap");
            }
        }
    }

    private boolean pressKeybind(String needle) {
        String lowerNeedle = needle.toLowerCase();
        for (KeyBinding keyBinding : mc.gameSettings.keyBindings) {
            if (keyBinding == null || keyBinding.getKeyCode() == 0) {
                continue;
            }
            String desc = keyBinding.getKeyDescription();
            if (desc != null && desc.toLowerCase().contains(lowerNeedle)) {
                KeyBinding.onTick(keyBinding.getKeyCode());
                return true;
            }
        }
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.player != null && System.currentTimeMillis() - lastStatsRefresh > 750L) {
            updateStatsData();
            updateTopData();
            lastStatsRefresh = System.currentTimeMillis();
        }

        float fade = Math.min(1F, (System.currentTimeMillis() - openTime) / 380F);
        drawGradientRect(0, 0, width, height, (int) (fade * 68) << 24, (int) (fade * 108) << 24);
        drawGradientRect(0, 0, width, height / 2, 0x061B3A54, 0x00000000);
        drawGradientRect(0, height / 2, width, height, 0x0C04131F, 0x14000000);

        for (MenuParticle particle : particles) {
            particle.update(width, height);
            drawRect(particle.x, particle.y, particle.x + particle.size, particle.y + particle.size, 0x3000DFFF);
        }

        drawLeftGroupLabels();
        drawRightPanels(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawLeftGroupLabels() {
        String actions = "ДЕЙСТВИЯ";
        String settings = "НАСТРОЙКИ";
        MENU_FONT.drawString(actions, leftMenuX + 6, actionsLabelY, CYAN_ACCENT);
        MENU_FONT.drawString(settings, leftMenuX + 6, settingsLabelY, settingsExpanded ? CYAN_ACCENT : TEXT_SECONDARY);
        drawRect(leftMenuX + 6, actionsLabelY + 12, leftMenuX + 92, actionsLabelY + 13, 0x163EDFFF);
        drawRect(leftMenuX + 6, settingsLabelY + 12, leftMenuX + 92, settingsLabelY + 13, settingsExpanded ? 0x163EDFFF : 0x10FFFFFF);
    }

    private void drawRightPanels(int mouseX, int mouseY) {
        int panelW = Math.min(224, Math.max(196, width / 5));
        int panelX = width - panelW - Math.max(14, width / 70);
        int topPanelY = 20;
        int topPanelH = Math.max(146, height / 3 - 4);
        int bottomPanelY = topPanelY + topPanelH + 10;
        int bottomPanelH = height - bottomPanelY - 20;

        drawSoftPanel(panelX, topPanelY, panelW, topPanelH, PANEL_FILL, PANEL_EDGE);
        drawSoftPanel(panelX, bottomPanelY, panelW, bottomPanelH, PANEL_FILL, PANEL_EDGE);

        drawRect(panelX + 10, topPanelY + 30, panelX + panelW - 10, topPanelY + 31, 0x1236D4EC);

        String statsTitle = "ВАША СТАТИСТИКА";
        MENU_FONT.drawString(statsTitle, panelX + 10, bottomPanelY + 12, CYAN_ACCENT);

        topScroll.draw(mouseX, mouseY);
        statsScroll.draw(mouseX, mouseY);
    }

    private void drawSoftPanel(int x, int y, int w, int h, int fillColor, int glowColor) {
        drawRect(x, y, x + w, y + h, fillColor);
        drawRect(x, y, x + w, y + 1, glowColor);
        drawRect(x, y + 1, x + w, y + h, PANEL_SOFT);
        drawRect(x, y + h - 1, x + w, y + h, 0x10182C36);
        drawRect(x, y, x + 1, y + h, 0x0C5CCEEA);
        drawRect(x + w - 1, y, x + w, y + h, 0x0C5CCEEA);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        topScroll.handleScroll(wheel);
        statsScroll.handleScroll(wheel);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        topScroll.mouseClicked(mouseX, mouseY);
        statsScroll.mouseClicked(mouseX, mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        topScroll.mouseReleased();
        statsScroll.mouseReleased();
    }

    public static class UILeftButton extends GuiButton {
        private static final int RADIUS = 9;
        private static final int BG_COLOR = 0xB0181C27;
        private static final int HOVER_BG_COLOR = 0xCC242845;
        private static final int BORDER_COLOR = 0x40FFFFFF;
        private static final int TEXT_COLOR = TEXT_PRIMARY;
        private static final int SEGMENTS = 18;

        private boolean lastHovered;

        public UILeftButton(int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }

            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            if (hovered && !lastHovered) {
                playUiSound(HOVER_SOUND, 0.4F, 1.0F);
            }
            lastHovered = hovered;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            GlStateManager.disableTexture2D();

            drawRoundedRect(x, y, width, height, RADIUS, hovered ? HOVER_BG_COLOR : BG_COLOR);
            drawRoundedOutline(x, y, width, height, RADIUS, BORDER_COLOR);

            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();

            int textWidth = MENU_FONT.getStringWidth(displayString);
            float textX = x + Math.max(8, (width - textWidth) / 2.0F);
            float textY = y + (height - MENU_FONT.getLineHeight()) / 2.0F + 1.0F;
            MENU_FONT.drawString(displayString, textX, textY, TEXT_COLOR);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.55F, 1.0F);
        }

        private void drawRoundedRect(int x, int y, int width, int height, int radius, int color) {
            float a = (color >> 24 & 255) / 255.0F;
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(x + width / 2.0, y + height / 2.0, 0).color(r, g, b, a).endVertex();

            addArc(buffer, x + radius, y + radius, radius, 180, 270, r, g, b, a);
            addArc(buffer, x + width - radius, y + radius, radius, 270, 360, r, g, b, a);
            addArc(buffer, x + width - radius, y + height - radius, radius, 0, 90, r, g, b, a);
            addArc(buffer, x + radius, y + height - radius, radius, 90, 180, r, g, b, a);
            tess.draw();
        }

        private void drawRoundedOutline(int x, int y, int width, int height, int radius, int color) {
            float a = (color >> 24 & 255) / 255.0F;
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            GL11.glLineWidth(1.1F);
            buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

            addArc(buffer, x + radius, y + radius, radius, 180, 270, r, g, b, a);
            addArc(buffer, x + width - radius, y + radius, radius, 270, 360, r, g, b, a);
            addArc(buffer, x + width - radius, y + height - radius, radius, 0, 90, r, g, b, a);
            addArc(buffer, x + radius, y + height - radius, radius, 90, 180, r, g, b, a);
            tess.draw();
        }

        private void addArc(BufferBuilder buffer, double cx, double cy, double radius,
                            int startAngle, int endAngle, float r, float g, float b, float a) {
            for (int angle = startAngle; angle <= endAngle; angle += 90 / SEGMENTS) {
                double radians = Math.toRadians(angle);
                double px = cx + Math.cos(radians) * radius;
                double py = cy + Math.sin(radians) * radius;
                buffer.pos(px, py, 0).color(r, g, b, a).endVertex();
            }
        }
    }

    public static class UITopButton extends GuiButton {
        private final int idx;

        public UITopButton(int id, int x, int y, int width, int height, String text, int idx) {
            super(id, x, y, width, height, text);
            this.idx = idx;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }

            boolean active = EscapeMenu.currentTop == idx;
            boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawRect(x, y, x + width, y + height, active ? 0x4425C6E8 : hovered ? 0x1BFFFFFF : 0x090E1520);
            if (active) {
                drawRect(x, y + height - 2, x + width, y + height, 0xFF53E5FF);
            }

            int textWidth = MENU_FONT.getStringWidth(displayString);
            float textX = x + (width - textWidth) / 2.0F;
            float textY = y + (height - MENU_FONT.getLineHeight()) / 2.0F + 1.0F;
            MENU_FONT.drawString(displayString, textX, textY, active ? CYAN_ACCENT : hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.5F, 1.0F);
        }
    }

    public static class UIScrollList {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private float scroll;
        private float velocity;
        private boolean dragging;
        private List<String> data = new ArrayList<>();

        public UIScrollList(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        public void handleScroll(int wheel) {
            if (wheel != 0) {
                velocity += wheel > 0 ? -14 : 14;
            }
        }

        public void mouseClicked(int mouseX, int mouseY) {
            if (mouseX >= x + width - 5 && mouseX <= x + width + 5 && mouseY >= y && mouseY <= y + height) {
                dragging = true;
            }
        }

        public void mouseReleased() {
            dragging = false;
        }

        public void draw(int mouseX, int mouseY) {
            int totalHeight = data.size() * LIST_LINE_HEIGHT;
            velocity *= 0.8F;
            scroll += velocity;

            int maxScroll = Math.max(0, totalHeight - height);
            if (dragging && totalHeight > 0) {
                float percent = (float) (mouseY - y) / (float) height;
                scroll = percent * totalHeight;
                velocity = 0;
            }
            scroll = Math.max(0, Math.min(scroll, maxScroll));

            for (int i = 0; i < data.size(); i++) {
                int lineY = y + (i * LIST_LINE_HEIGHT) - (int) scroll;
                if (lineY >= y && lineY <= y + height - 10) {
                    MENU_FONT.drawString("• " + data.get(i), x + 5, lineY, i == 0 ? TEXT_PRIMARY : TEXT_SECONDARY);
                }
            }

            drawRect(x + width - 2, y, x + width, y + height, 0x15FFFFFF);
            if (totalHeight > height) {
                int thumbHeight = Math.max(10, (int) ((float) height / totalHeight * height));
                int thumbY = y + (int) (scroll / totalHeight * height);
                drawRect(x + width - 2, thumbY, x + width, thumbY + thumbHeight, 0xFF52E5FF);
            }
        }
    }

    public static class IconButton extends GuiButton {
        private final ResourceLocation icon;

        public IconButton(int id, int x, int y, ResourceLocation icon) {
            super(id, x, y, 20, 20, "");
            this.icon = icon;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawRect(x, y, x + width, y + height, hovered ? 0x2253E6FF : 0x0F000000);
            mc.getTextureManager().bindTexture(icon);
            GlStateManager.enableBlend();
            drawModalRectWithCustomSizedTexture(x + 2, y + 2, 0, 0, 16, 16, 16, 16);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.5F, 1.0F);
        }
    }

    public static class TextIconButton extends GuiButton {
        private final String label;

        public TextIconButton(int id, int x, int y, String label) {
            super(id, x, y, 20, 20, "");
            this.label = label;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                return;
            }
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            drawRect(x, y, x + width, y + height, hovered ? 0x2253E6FF : 0x0F000000);
            int textWidth = MENU_FONT.getStringWidth(label);
            MENU_FONT.drawString(label, x + (width - textWidth) / 2.0F, y + 7, hovered ? TEXT_PRIMARY : TEXT_SECONDARY);
        }

        @Override
        public void playPressSound(net.minecraft.client.audio.SoundHandler soundHandlerIn) {
            playUiSound(CLICK_SOUND, 0.5F, 1.0F);
        }
    }

    class MenuParticle {
        int x;
        int y;
        int size;

        MenuParticle(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = rand.nextInt(2) + 1;
        }

        void update(int width, int height) {
            x += DIRS[dirIdx][0] * size;
            y += DIRS[dirIdx][1] * size;
            if (y < 0) {
                y = height;
            }
            if (y > height) {
                y = 0;
            }
            if (x < 0) {
                x = width;
            }
            if (x > width) {
                x = 0;
            }
        }
    }
}
