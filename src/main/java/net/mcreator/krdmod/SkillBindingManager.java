package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public final class SkillBindingManager {
    public static final class SkillDefinition {
        private final String id;
        private final String familyId;
        private final String familyName;
        private final String displayName;
        private final String command;
        private final long cooldownMs;
        private final ResourceLocation breathIcon;
        private final ResourceLocation kataIcon;

        private SkillDefinition(String id, String familyId, String familyName, String displayName, String command, long cooldownMs,
                                ResourceLocation breathIcon, ResourceLocation kataIcon) {
            this.id = id;
            this.familyId = familyId;
            this.familyName = familyName;
            this.displayName = displayName;
            this.command = command;
            this.cooldownMs = cooldownMs;
            this.breathIcon = breathIcon;
            this.kataIcon = kataIcon;
        }

        public String getId() {
            return id;
        }

        public String getFamilyId() {
            return familyId;
        }

        public String getFamilyName() {
            return familyName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCommand() {
            return command;
        }

        public long getCooldownMs() {
            return cooldownMs;
        }

        public ResourceLocation getBreathIcon() {
            return breathIcon;
        }

        public ResourceLocation getKataIcon() {
            return kataIcon;
        }
    }

    private static final ResourceLocation VODA_BREATH_ICON = new ResourceLocation("krd_mod", "textures/breath/voda.png");
    private static final ResourceLocation GROM_BREATH_ICON = new ResourceLocation("krd_mod", "textures/breath/grom.png");
    private static final ResourceLocation VETER_BREATH_ICON = new ResourceLocation("krd_mod", "textures/breath/veter.png");
    private static final ResourceLocation ZMEI_BREATH_ICON = new ResourceLocation("krd_mod", "textures/breath/zmei.png");
    private static final ResourceLocation PLAMYA_BREATH_ICON = new ResourceLocation("krd_mod", "textures/breath/plamya.png");
    private static final List<SkillDefinition> DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
            water("wk1", "Вода", "Ката 1", 4000L, "kata_voda_item1.png"),
            water("wk2", "Вода", "Ката 2", 6000L, "kata_voda_item2.png"),
            water("wk3", "Вода", "Ката 3", 7000L, "kata_voda_item3.png"),
            water("wk4", "Вода", "Ката 4", 8000L, "kata_voda_item4.png"),
            water("wk6", "Вода", "Ката 6", 10000L, "kata_voda_item6.png"),
            water("wk8", "Вода", "Ката 8", 9000L, "kata_voda_item8.png"),
            water("wk9", "Вода", "Ката 9", 10000L, "kata_voda_item9.png"),
            water("wk10", "Вода", "Ката 10", 11000L, null),
            water("wk11", "Вода", "Ката 11", 18000L, "kata_voda_item11.png"),
            thunder("lk1", "Гром", "Ката 1", 4000L, "kata_grom_item1.png"),
            thunder("lk2", "Гром", "Ката 2", 7000L, "kata_grom_item2.png"),
            thunder("lk3", "Гром", "Ката 3", 8000L, "kata_grom_item3.png"),
            thunder("lk4", "Гром", "Ката 4", 8000L, "kata_grom_item4.png"),
            thunder("lk5", "Гром", "Ката 5", 10000L, "kata_grom_item5.png"),
            thunder("lk6", "Гром", "Ката 6", 11000L, "kata_grom_item6.png"),
            thunder("lk7", "Гром", "Ката 7", 13000L, "kata_grom_item7.png"),
            wind("vk1", "Ветер", "Ката 1", 5000L, "kata_weter_item1.png"),
            wind("vk2", "Ветер", "Ката 2", 7000L, "kata_weter_item2.png"),
            wind("vk3", "Ветер", "Ката 3", 8000L, "kata_weter_item3.png"),
            wind("vk4", "Ветер", "Ката 4", 9000L, "kata_weter_item4.png"),
            wind("vk5", "Ветер", "Ката 5", 10000L, "kata_weter_item5.png"),
            wind("vk7", "Ветер", "Ката 7", 12000L, "kata_weter_item7.png"),
            wind("vk8", "Ветер", "Ката 8", 13000L, "kata_weter_item8.png"),
            snake("zk1", "Змея", "Ката 1", 5000L, "kata_snake_item1.png"),
            snake("zk2", "Змея", "Ката 2", 7000L, "kata_snake_item2.png"),
            snake("zk3", "Змея", "Ката 3", 9000L, "kata_snake_item3.png"),
            snake("zk4", "Змея", "Ката 4", 9000L, "kata_snake_item4.png"),
            snake("zk5", "Змея", "Ката 5", 11000L, "kata_snake_item5.png"),
            flame("fk1", "Пламя", "Ката 1", 5000L, "kata_flame_item1.png"),
            flame("fk2", "Пламя", "Ката 2", 7000L, "kata_flame_item2.png"),
            flame("fk3", "Пламя", "Ката 3", 8000L, "kata_flame_item3.png"),
            flame("fk4", "Пламя", "Ката 4", 9000L, "kata_flame_item4.png"),
            flame("fk5", "Пламя", "Ката 5", 11000L, "kata_flame_item5.png"),
            flame("fk9", "Пламя", "Ката 9", 15000L, "kata_flame_item9.png")
    ));
    private static final Map<String, SkillDefinition> BY_ID = new LinkedHashMap<>();
    private static final Map<String, Integer> KEY_CODES = new HashMap<>();
    private static final Map<String, Long> COOLDOWNS = new HashMap<>();
    private static final Map<String, Boolean> PRESSED = new HashMap<>();
    private static boolean loaded;

    static {
        for (SkillDefinition definition : DEFINITIONS) {
            BY_ID.put(definition.getId(), definition);
        }
    }

    private SkillBindingManager() {
    }

    private static SkillDefinition water(String command, String familyName, String displayName, long cooldownMs, String kataIconFile) {
        return new SkillDefinition(command, "voda", familyName, displayName, command, cooldownMs, VODA_BREATH_ICON,
                kataIconFile == null ? VODA_BREATH_ICON : new ResourceLocation("krd_mod", "textures/katas/" + kataIconFile));
    }

    private static SkillDefinition thunder(String command, String familyName, String displayName, long cooldownMs, String kataIconFile) {
        return new SkillDefinition(command, "grom", familyName, displayName, command, cooldownMs, GROM_BREATH_ICON,
                kataIconFile == null ? GROM_BREATH_ICON : new ResourceLocation("krd_mod", "textures/katas/" + kataIconFile));
    }

    private static SkillDefinition wind(String command, String familyName, String displayName, long cooldownMs, String kataIconFile) {
        return new SkillDefinition(command, "veter", familyName, displayName, command, cooldownMs, VETER_BREATH_ICON,
                kataIconFile == null ? VETER_BREATH_ICON : new ResourceLocation("krd_mod", "textures/katas/" + kataIconFile));
    }

    private static SkillDefinition snake(String command, String familyName, String displayName, long cooldownMs, String kataIconFile) {
        return new SkillDefinition(command, "zmei", familyName, displayName, command, cooldownMs, ZMEI_BREATH_ICON,
                kataIconFile == null ? ZMEI_BREATH_ICON : new ResourceLocation("krd_mod", "textures/katas/" + kataIconFile));
    }

    private static SkillDefinition flame(String command, String familyName, String displayName, long cooldownMs, String kataIconFile) {
        return new SkillDefinition(command, "plamya", familyName, displayName, command, cooldownMs, PLAMYA_BREATH_ICON,
                kataIconFile == null ? PLAMYA_BREATH_ICON : new ResourceLocation("krd_mod", "textures/katas/" + kataIconFile));
    }

    public static SkillDefinition getDefinition(String skillId) {
        ensureLoaded();
        return skillId == null ? null : BY_ID.get(skillId.toLowerCase(Locale.ROOT));
    }

    public static ResourceLocation getBreathIcon(String familyId) {
        String normalized = familyId == null ? "" : familyId.toLowerCase(Locale.ROOT);
        if ("voda".equals(normalized)) {
            return VODA_BREATH_ICON;
        }
        if ("grom".equals(normalized)) {
            return GROM_BREATH_ICON;
        }
        if ("veter".equals(normalized)) {
            return VETER_BREATH_ICON;
        }
        if ("zmei".equals(normalized)) {
            return ZMEI_BREATH_ICON;
        }
        if ("plamya".equals(normalized)) {
            return PLAMYA_BREATH_ICON;
        }
        return VODA_BREATH_ICON;
    }

    public static List<SkillDefinition> getDefinitions() {
        ensureLoaded();
        return DEFINITIONS;
    }

    public static List<SkillDefinition> getDefinitionsByFamily(String familyId) {
        ensureLoaded();
        List<SkillDefinition> result = new ArrayList<>();
        for (SkillDefinition definition : DEFINITIONS) {
            if (definition.getFamilyId().equals(familyId)) {
                result.add(definition);
            }
        }
        return result;
    }

    public static List<SkillDefinition> getUnlockedDefinitions(Minecraft mc) {
        ensureLoaded();
        List<SkillDefinition> result = new ArrayList<>();
        for (SkillDefinition definition : DEFINITIONS) {
            if (ServerLevelBridge.getSkillLevel(mc, definition.getId()) > 0) {
                result.add(definition);
            }
        }
        return result;
    }

    public static String getKeyName(String skillId) {
        ensureLoaded();
        int keyCode = KEY_CODES.getOrDefault(skillId, Keyboard.KEY_NONE);
        if (keyCode == Keyboard.KEY_NONE) {
            return "Не назначено";
        }
        String name = Keyboard.getKeyName(keyCode);
        return name == null || name.trim().isEmpty() ? ("KEY " + keyCode) : name.toUpperCase(Locale.ROOT);
    }

    public static int getKeyCode(String skillId) {
        ensureLoaded();
        return KEY_CODES.getOrDefault(skillId, Keyboard.KEY_NONE);
    }

    public static void setKeyCode(String skillId, int keyCode) {
        ensureLoaded();
        if (!BY_ID.containsKey(skillId)) {
            return;
        }
        for (SkillDefinition definition : DEFINITIONS) {
            if (!definition.getId().equals(skillId) && KEY_CODES.getOrDefault(definition.getId(), Keyboard.KEY_NONE) == keyCode) {
                KEY_CODES.put(definition.getId(), Keyboard.KEY_NONE);
            }
        }
        KEY_CODES.put(skillId, keyCode);
        save();
    }

    public static void clearAll() {
        ensureLoaded();
        for (SkillDefinition definition : DEFINITIONS) {
            KEY_CODES.put(definition.getId(), Keyboard.KEY_NONE);
        }
        save();
    }

    public static long getRemainingCooldown(String skillId) {
        ensureLoaded();
        long readyAt = COOLDOWNS.getOrDefault(skillId, 0L);
        return Math.max(0L, readyAt - System.currentTimeMillis());
    }

    public static String getCooldownLabel(String skillId) {
        long remaining = getRemainingCooldown(skillId);
        if (remaining <= 0L) {
            return "Готово";
        }
        return String.format(Locale.US, "%.1fс", remaining / 1000.0D);
    }

    public static float getCooldownProgress(String skillId) {
        ensureLoaded();
        SkillDefinition definition = BY_ID.get(skillId);
        if (definition == null || definition.getCooldownMs() <= 0L) {
            return 1.0F;
        }
        long remaining = getRemainingCooldown(skillId);
        if (remaining <= 0L) {
            return 1.0F;
        }
        float ready = 1.0F - (remaining / (float) definition.getCooldownMs());
        return Math.max(0.0F, Math.min(1.0F, ready));
    }

    public static List<SkillDefinition> getBoundDefinitions() {
        return getBoundDefinitions(Minecraft.getMinecraft());
    }

    public static List<SkillDefinition> getBoundDefinitions(Minecraft mc) {
        ensureLoaded();
        List<SkillDefinition> result = new ArrayList<>();
        for (SkillDefinition definition : DEFINITIONS) {
            if (getKeyCode(definition.getId()) != Keyboard.KEY_NONE && ServerLevelBridge.getSkillLevel(mc, definition.getId()) > 0) {
                result.add(definition);
            }
        }
        return result;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ensureLoaded();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) {
            PRESSED.clear();
            return;
        }

        if (mc.currentScreen != null) {
            releaseAllPressed();
            return;
        }

        for (SkillDefinition definition : DEFINITIONS) {
            int keyCode = getKeyCode(definition.getId());
            if (keyCode == Keyboard.KEY_NONE) {
                PRESSED.put(definition.getId(), Boolean.FALSE);
                continue;
            }

            boolean down = Keyboard.isKeyDown(keyCode);
            boolean wasDown = PRESSED.getOrDefault(definition.getId(), Boolean.FALSE);
            if (down && !wasDown) {
                triggerSkill(mc, definition);
            }
            PRESSED.put(definition.getId(), down);
        }
    }

    @SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        ensureLoaded();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.gameSettings == null || mc.currentScreen != null) {
            return;
        }

        List<SkillDefinition> bound = getBoundDefinitions(mc);
        if (bound.isEmpty()) {
            return;
        }

        ScaledResolution resolution = event.getResolution();
        int iconSize = 16;
        int gap = 4;
        int columns = Math.min(4, Math.max(1, bound.size()));
        int xStart = resolution.getScaledWidth() - 12 - iconSize;
        int yBase = resolution.getScaledHeight() - 6 - iconSize;

        for (int i = 0; i < bound.size(); i++) {
            SkillDefinition definition = bound.get(i);
            int column = i % columns;
            int row = i / columns;
            int x = xStart - column * (iconSize + gap);
            int y = yBase - row * (iconSize + gap);
            drawCooldownIcon(mc, definition, x, y, iconSize);
        }
    }

    private static void triggerSkill(Minecraft mc, SkillDefinition definition) {
        long now = System.currentTimeMillis();
        long readyAt = COOLDOWNS.getOrDefault(definition.getId(), 0L);
        if (readyAt > now) {
            double seconds = (readyAt - now) / 1000.0D;
            mc.player.sendStatusMessage(new TextComponentString("КД: " + String.format(Locale.US, "%.1f", seconds) + "с"), true);
            return;
        }

        mc.player.sendChatMessage("/" + definition.getCommand());
        COOLDOWNS.put(definition.getId(), now + definition.getCooldownMs());
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }

        loaded = true;
        for (SkillDefinition definition : DEFINITIONS) {
            KEY_CODES.put(definition.getId(), Keyboard.KEY_NONE);
        }

        File file = getConfigFile();
        if (!file.exists()) {
            return;
        }

        Properties properties = new Properties();
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            properties.load(input);
            for (SkillDefinition definition : DEFINITIONS) {
                String value = properties.getProperty(definition.getId());
                if (value == null) {
                    continue;
                }
                try {
                    KEY_CODES.put(definition.getId(), Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void save() {
        File file = getConfigFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Properties properties = new Properties();
        for (SkillDefinition definition : DEFINITIONS) {
            properties.setProperty(definition.getId(), Integer.toString(KEY_CODES.getOrDefault(definition.getId(), Keyboard.KEY_NONE)));
        }

        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
            properties.store(output, "KRD skill bindings");
        } catch (IOException ignored) {
        }
    }

    private static File getConfigFile() {
        return new File(Minecraft.getMinecraft().mcDataDir, "config/krd_skill_bindings.properties");
    }

    private static void releaseAllPressed() {
        for (SkillDefinition definition : DEFINITIONS) {
            PRESSED.put(definition.getId(), Boolean.FALSE);
        }
    }

    private static void drawIcon(Minecraft mc, ResourceLocation icon, int x, int y, int size) {
        mc.getTextureManager().bindTexture(icon);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
    }

    private static void drawCooldownIcon(Minecraft mc, SkillDefinition definition, int x, int y, int size) {
        float progress = getCooldownProgress(definition.getId());
        mc.getTextureManager().bindTexture(definition.getKataIcon());
        GlStateManager.enableBlend();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.28F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);

        int visibleHeight = Math.max(0, Math.min(size, Math.round(size * progress)));
        if (visibleHeight > 0) {
            int srcY = size - visibleHeight;
            int drawY = y + size - visibleHeight;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(x, drawY, 0, srcY, size, visibleHeight, size, size);
        }

        String keyName = getKeyName(definition.getId());
        if (!"Не назначено".equals(keyName)) {
            int textWidth = mc.fontRenderer.getStringWidth(keyName);
            int textX = x + size - textWidth;
            int textY = y + size - 8;
            mc.fontRenderer.drawStringWithShadow(keyName, textX, textY, 0xF4F8FF);
        }

    }
}
