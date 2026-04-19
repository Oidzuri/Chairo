package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public class KRDTabOverlay extends Gui {
    private static final int PANEL_MAX_WIDTH = 640;
    private static final int PANEL_MIN_WIDTH = 420;
    private static final int PANEL_PADDING = 12;
    private static final int HEADER_HEIGHT = 38;
    private static final int FOOTER_HEIGHT = 12;
    private static final int CARD_HEIGHT = 56;
    private static final int CARD_GAP = 6;
    private static final int HEAD_SIZE = 28;
    private static final int HEAD_MARGIN = 8;
    private static final int TARGET_CARD_WIDTH = 180;
    private static final int ACCENT = 0xFF24D3D8;
    private static final int ACCENT_SOFT = 0x7F2CE0FF;
    private static final int PANEL_BG = 0xC0101A22;
    private static final int PANEL_SHADOW = 0x38000000;
    private static final int CARD_BG = 0xD3223442;
    private static final int CARD_BG_SELF = 0xF03A4860;
    private static final int CARD_BORDER = 0x66435F73;
    private static final int CARD_BORDER_SELF = 0xFF5AEBFF;
    private static final int TEXT_PRIMARY = 0xFFF4F8FB;
    private static final int TEXT_SECONDARY = 0xFFABC1CC;
    private static final int LEVEL_BADGE = 0xCC123848;
    private static final int CLAN_BADGE = 0xCC41214D;
    private static final int EMPTY_BADGE = 0x66303A40;
    private static final int RANK_BADGE = 0x66405870;
    private static final int FOOTER_TEXT = 0xFFA6C5D2;
    private static final Map<String, AvatarTexture> AVATAR_CACHE = new HashMap<>();
    private static final Map<String, byte[]> SYNCED_AVATAR_BYTES = new HashMap<>();
    private static final Map<String, ResourceLocation> SYNCED_AVATAR_TEXTURES = new HashMap<>();

    private static final class AvatarTexture {
        private final long lastModified;
        private final ResourceLocation texture;

        private AvatarTexture(long lastModified, ResourceLocation texture) {
            this.lastModified = lastModified;
            this.texture = texture;
        }
    }

    @SubscribeEvent
    public static void hideVanillaTab(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderCustomTab(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.gameSettings == null || mc.fontRenderer == null) {
            return;
        }

        KeyBinding key = mc.gameSettings.keyBindPlayerList;
        if (key == null || !key.isKeyDown()) {
            return;
        }

        List<ServerLevelBridge.PlayerSnapshot> players = sortPlayers(mc, ServerLevelBridge.getOnlineSnapshots(mc));
        if (players.isEmpty()) {
            return;
        }

        ScaledResolution sr = event.getResolution();
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        int preferredThreeColumnWidth = PANEL_PADDING * 2 + TARGET_CARD_WIDTH * 3 + CARD_GAP * 2;
        int panelWidth = Math.min(preferredThreeColumnWidth, screenWidth - 180);
        panelWidth = Math.min(panelWidth, PANEL_MAX_WIDTH);
        panelWidth = Math.max(PANEL_MIN_WIDTH, panelWidth);
        int innerWidth = panelWidth - PANEL_PADDING * 2;
        int columns = innerWidth >= 300 ? 3 : (innerWidth >= 200 ? 2 : 1);
        int cardWidth = (innerWidth - CARD_GAP * (columns - 1)) / columns;
        int rows = Math.max(1, (int) Math.ceil(players.size() / (double) columns));
        int maxBodyHeight = Math.max(CARD_HEIGHT, screenHeight - 96 - HEADER_HEIGHT - FOOTER_HEIGHT - PANEL_PADDING * 2);
        int maxRowsVisible = Math.max(1, (maxBodyHeight + CARD_GAP) / (CARD_HEIGHT + CARD_GAP));
        int visibleRows = Math.min(rows, maxRowsVisible);
        int shownPlayers = Math.min(players.size(), visibleRows * columns);

        int panelHeight = PANEL_PADDING + HEADER_HEIGHT + 3 + visibleRows * CARD_HEIGHT + Math.max(0, visibleRows - 1) * CARD_GAP + FOOTER_HEIGHT + 2;
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = Math.max(8, (screenHeight - panelHeight) / 2 - 82);

        drawPanel(panelX, panelY, panelWidth, panelHeight);
        drawHeader(mc, players, panelX, panelY, panelWidth);

        int gridX = panelX + PANEL_PADDING;
        int gridY = panelY + PANEL_PADDING + HEADER_HEIGHT + 3;
        for (int i = 0; i < shownPlayers; i++) {
            int column = i % columns;
            int row = i / columns;
            int cardX = gridX + column * (cardWidth + CARD_GAP);
            int cardY = gridY + row * (CARD_HEIGHT + CARD_GAP);
            drawPlayerCard(mc, players.get(i), cardX, cardY, cardWidth, CARD_HEIGHT);
        }

        drawFooter(mc, panelX, panelY + panelHeight - PANEL_PADDING - FOOTER_HEIGHT, panelWidth, players.size(), shownPlayers);
    }

    private static List<ServerLevelBridge.PlayerSnapshot> sortPlayers(Minecraft mc, List<ServerLevelBridge.PlayerSnapshot> input) {
        ArrayList<ServerLevelBridge.PlayerSnapshot> players = new ArrayList<>(input);
        final String selfName = mc.player == null ? "" : mc.player.getName();
        players.sort(Comparator
                .comparing((ServerLevelBridge.PlayerSnapshot snapshot) -> !snapshot.name.equalsIgnoreCase(selfName))
                .thenComparingInt(KRDTabOverlay::getProfessionPriority)
                .thenComparing(snapshot -> snapshot.name.toLowerCase(Locale.ROOT)));
        return players;
    }

    private static void drawPanel(int x, int y, int width, int height) {
        drawRect(x + 5, y + 5, x + width + 5, y + height + 5, PANEL_SHADOW);
        drawRect(x, y, x + width, y + height, PANEL_BG);
        drawRect(x, y, x + width, y + 2, ACCENT_SOFT);
        drawRect(x, y, x + 2, y + height, 0x442CE0FF);
        drawRect(x + width - 2, y, x + width, y + height, 0x221C2C35);
        drawRect(x, y + height - 2, x + width, y + height, 0x221C2C35);
        drawRect(x + PANEL_PADDING, y + PANEL_PADDING + HEADER_HEIGHT, x + width - PANEL_PADDING, y + PANEL_PADDING + HEADER_HEIGHT + 1, 0x2232E0FF);
    }

    private static void drawHeader(Minecraft mc, List<ServerLevelBridge.PlayerSnapshot> players, int x, int y, int width) {
        int totalPlayers = players.size();
        int adminOnline = 0;
        for (ServerLevelBridge.PlayerSnapshot snapshot : players) {
            if (isAdmin(snapshot.adminRole)) {
                adminOnline++;
            }
        }

        String title = "\u0418\u0433\u0440\u043e\u043a\u0438 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435";
        String subtitle = totalPlayers + " online  |  \u0410\u0434\u043c\u0438\u043d\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u0438 online: " + adminOnline;
        mc.fontRenderer.drawStringWithShadow(title, x + PANEL_PADDING, y + PANEL_PADDING, TEXT_PRIMARY);
        mc.fontRenderer.drawStringWithShadow(subtitle, x + PANEL_PADDING, y + PANEL_PADDING + 11, TEXT_SECONDARY);

        drawRect(x + PANEL_PADDING, y + PANEL_PADDING + 22, x + width - PANEL_PADDING, y + PANEL_PADDING + 23, 0x18FFFFFF);

        String tip = "TAB";
        int tipWidth = mc.fontRenderer.getStringWidth(tip) + 10;
        int tipX = x + width - PANEL_PADDING - tipWidth;
        int tipY = y + PANEL_PADDING;
        drawRect(tipX, tipY, tipX + tipWidth, tipY + 12, 0x8832E0FF);
        mc.fontRenderer.drawString(tip, tipX + 5, tipY + 2, 0xFF07141B, false);
    }

    private static void drawPlayerCard(Minecraft mc, ServerLevelBridge.PlayerSnapshot snapshot, int x, int y, int width, int height) {
        boolean self = mc.player != null && snapshot.name.equalsIgnoreCase(mc.player.getName());
        int fillColor = self ? CARD_BG_SELF : CARD_BG;
        int borderColor = self ? CARD_BORDER_SELF : CARD_BORDER;

        if (self) {
            drawRect(x - 1, y - 1, x + width + 2, y + height + 2, 0x4430E8FF);
        }
        drawRect(x + 1, y + 1, x + width + 1, y + height + 1, 0x14000000);
        drawRect(x, y, x + width, y + height, fillColor);
        drawRect(x, y, x + width, y + 1, borderColor);
        drawRect(x, y + height - 1, x + width, y + height, 0x33000000);
        drawRect(x, y, x + 1, y + height, borderColor);
        drawRect(x + width - 1, y, x + width, y + height, 0x33000000);
        if (self) {
            drawRect(x + 1, y + 1, x + width - 1, y + 3, 0x2238F4FF);
        }

        int accentColor = getAccentColor(snapshot);
        drawRect(x + 1, y + 1, x + 3, y + height - 1, accentColor);

        int headX = x + HEAD_MARGIN;
        int headY = y + (height - HEAD_SIZE) / 2;
        drawPlayerAvatar(mc, snapshot.name, headX, headY, HEAD_SIZE);

        int textX = headX + HEAD_SIZE + 7;
        int badgeRightMargin = 6;
        int levelTextWidth = Math.max(22, mc.fontRenderer.getStringWidth("Lv. 99") + 8);
        String roleText = isAdmin(snapshot.adminRole) ? trimToWidth(mc, snapshot.adminRole, 58) : compactRank(snapshot.rank);
        int rankTextWidth = Math.max(34, mc.fontRenderer.getStringWidth(roleText) + 6);
        int reservedRight = Math.max(levelTextWidth, rankTextWidth) + badgeRightMargin;
        int nameMaxWidth = Math.max(44, width - (textX - x) - reservedRight - 4);
        String name = trimToWidth(mc, snapshot.name, nameMaxWidth);
        mc.fontRenderer.drawStringWithShadow(name, textX, y + 5, TEXT_PRIMARY);

        String professionLine = trimToWidth(mc, primaryMeta(snapshot), nameMaxWidth);
        String secondaryLine = trimToWidth(mc, secondaryMeta(snapshot), nameMaxWidth);
        mc.fontRenderer.drawString(professionLine, textX, y + 16, TEXT_SECONDARY, false);
        if (!secondaryLine.isEmpty()) {
            mc.fontRenderer.drawString(secondaryLine, textX, y + 26, 0xFF93ABBA, false);
        }

        boolean admin = isAdmin(snapshot.adminRole);
        int roleBg = admin ? 0x886D3D18 : RANK_BADGE;
        drawBadge(mc, x + width - 6 - Math.max(22, mc.fontRenderer.getStringWidth(roleText) + 6), y + 5, roleText, roleBg, TEXT_PRIMARY);
        drawMetaBadges(mc, snapshot, x, y, width, height);
    }

    private static void drawMetaBadges(Minecraft mc, ServerLevelBridge.PlayerSnapshot snapshot, int x, int y, int width, int height) {
        String levelText = snapshot.level >= 0 ? "Lv. " + snapshot.level : "--";
        int levelWidth = Math.max(22, mc.fontRenderer.getStringWidth(levelText) + 8);
        String clanText = hasClan(snapshot)
                ? trimToWidth(mc, snapshot.clanName, Math.max(32, width / 2 - 12))
                : "\u0411\u0435\u0437 \u043a\u043b\u0430\u043d\u0430";
        int clanColor = hasClan(snapshot) ? CLAN_BADGE : EMPTY_BADGE;
        int clanTextColor = hasClan(snapshot) ? 0xFFFFC4E5 : 0xFFB9C8D1;
        int clanWidth = Math.max(30, mc.fontRenderer.getStringWidth(clanText) + 8);
        int badgeY = y + height - 12;
        drawBadge(mc, x + HEAD_MARGIN + HEAD_SIZE + 7, badgeY, clanText, clanColor, clanTextColor);
        drawBadge(mc, x + width - 6 - levelWidth, badgeY, levelText, snapshot.level >= 0 ? LEVEL_BADGE : EMPTY_BADGE, 0xFFB9F3FF);
    }

    private static void drawBadge(Minecraft mc, int x, int y, String text, int bgColor, int textColor) {
        int width = Math.max(14, mc.fontRenderer.getStringWidth(text) + 6);
        drawRect(x, y, x + width, y + 9, bgColor);
        drawRect(x, y, x + width, y + 1, 0x22FFFFFF);
        mc.fontRenderer.drawString(text, x + 3, y + 1, textColor, false);
    }

    private static void drawFooter(Minecraft mc, int x, int y, int width, int totalPlayers, int shownPlayers) {
        String footer = shownPlayers < totalPlayers
                ? "\u041f\u043e\u043a\u0430\u0437\u0430\u043d\u043e " + shownPlayers + " \u0438\u0437 " + totalPlayers
                : "";
        if (footer.isEmpty()) {
            return;
        }
        mc.fontRenderer.drawString(footer, x + PANEL_PADDING, y + 1, FOOTER_TEXT, false);
    }

    static void drawPlayerAvatar(Minecraft mc, String playerName, int x, int y, int size) {
        ResourceLocation customAvatar = getCustomAvatar(mc, playerName);
        if (customAvatar != null) {
            mc.getTextureManager().bindTexture(customAvatar);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            drawScaledCustomSizeModalRect(x, y, 0.0F, 0.0F, size, size, size, size, size, size);
            GlStateManager.disableBlend();
            drawRect(x, y + size - 1, x + size, y + size, 0x33000000);
            return;
        }

        NetworkPlayerInfo info = findPlayerInfo(mc, playerName);
        if (info == null) {
            drawFallbackHead(mc, playerName, x, y, size);
            return;
        }

        mc.getTextureManager().bindTexture(info.getLocationSkin());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, size, size, 64.0F, 64.0F);
        drawScaledCustomSizeModalRect(x, y, 40.0F, 8.0F, 8, 8, size, size, 64.0F, 64.0F);
        GlStateManager.disableBlend();
        drawRect(x, y + size - 1, x + size, y + size, 0x33000000);
    }

    private static ResourceLocation getCustomAvatar(Minecraft mc, String playerName) {
        if (mc == null || playerName == null || playerName.trim().isEmpty()) {
            return null;
        }

        String normalizedName = playerName.trim().toLowerCase(Locale.ROOT);
        ResourceLocation syncedAvatar = getSyncedAvatar(mc, normalizedName);
        if (syncedAvatar != null) {
            return syncedAvatar;
        }

        File avatarFile = findAvatarFile(new File(mc.mcDataDir, "krd_avatars"), playerName);
        if (avatarFile == null) {
            avatarFile = findAvatarFile(new File("C:\\Users\\Administrator\\ChairoLand\\Server-main\\krd_avatars"), playerName);
        }
        if (avatarFile == null || !avatarFile.isFile()) {
            return null;
        }

        String cacheKey = avatarFile.getAbsolutePath();
        long lastModified = avatarFile.lastModified();
        AvatarTexture cached = AVATAR_CACHE.get(cacheKey);
        if (cached != null && cached.lastModified == lastModified) {
            return cached.texture;
        }

        try {
            BufferedImage image = ImageIO.read(avatarFile);
            if (image == null) {
                return null;
            }
            ResourceLocation texture = mc.getTextureManager().getDynamicTextureLocation(
                    "krd_tab_avatar_" + playerName.toLowerCase(Locale.ROOT),
                    new DynamicTexture(image)
            );
            AVATAR_CACHE.put(cacheKey, new AvatarTexture(lastModified, texture));
            return texture;
        } catch (IOException ignored) {
            return null;
        }
    }

    static void receiveSyncedAvatar(String playerName, byte[] data) {
        if (playerName == null || playerName.trim().isEmpty() || data == null || data.length == 0) {
            return;
        }
        String normalized = playerName.trim().toLowerCase(Locale.ROOT);
        SYNCED_AVATAR_BYTES.put(normalized, data.clone());
        SYNCED_AVATAR_TEXTURES.remove(normalized);
    }

    private static ResourceLocation getSyncedAvatar(Minecraft mc, String normalizedName) {
        if (mc == null || normalizedName == null || normalizedName.isEmpty()) {
            return null;
        }

        ResourceLocation cachedTexture = SYNCED_AVATAR_TEXTURES.get(normalizedName);
        if (cachedTexture != null) {
            return cachedTexture;
        }

        byte[] bytes = SYNCED_AVATAR_BYTES.get(normalizedName);
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                return null;
            }
            ResourceLocation texture = mc.getTextureManager().getDynamicTextureLocation(
                    "krd_synced_avatar_" + normalizedName,
                    new DynamicTexture(image)
            );
            SYNCED_AVATAR_TEXTURES.put(normalizedName, texture);
            return texture;
        } catch (IOException ignored) {
            return null;
        }
    }

    private static File findAvatarFile(File avatarsDir, String playerName) {
        if (avatarsDir == null || !avatarsDir.isDirectory()) {
            return null;
        }

        String base = playerName.trim();
        String lower = base.toLowerCase(Locale.ROOT);
        String[] names = new String[]{
                base + ".png", lower + ".png",
                base + ".jpg", lower + ".jpg",
                base + ".jpeg", lower + ".jpeg"
        };
        for (String name : names) {
            File candidate = new File(avatarsDir, name);
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }

    private static void drawFallbackHead(Minecraft mc, String playerName, int x, int y, int size) {
        int color = getAccentColor(playerName);
        drawRect(x, y, x + size, y + size, 0xFF102029);
        drawRect(x + 1, y + 1, x + size - 1, y + size - 1, color);
        String initials = playerName == null || playerName.isEmpty()
                ? "?"
                : playerName.substring(0, Math.min(2, playerName.length())).toUpperCase(Locale.ROOT);
        int textX = x + (size - mc.fontRenderer.getStringWidth(initials)) / 2;
        int textY = y + (size - 8) / 2;
        mc.fontRenderer.drawString(initials, textX, textY, 0xFF041015, false);
    }

    private static NetworkPlayerInfo findPlayerInfo(Minecraft mc, String playerName) {
        if (mc == null || mc.getConnection() == null || playerName == null) {
            return null;
        }
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
            if (info != null && info.getGameProfile() != null && playerName.equalsIgnoreCase(info.getGameProfile().getName())) {
                return info;
            }
        }
        return null;
    }

    private static String primaryMeta(ServerLevelBridge.PlayerSnapshot snapshot) {
        String profession = cleanup(snapshot.profession);
        if (!profession.isEmpty() && !isNoneValue(profession)) {
            return profession;
        }
        String rank = cleanup(snapshot.rank);
        if (!rank.isEmpty()) {
            return rank;
        }
        return "\u0418\u0433\u0440\u043e\u043a";
    }

    private static String secondaryMeta(ServerLevelBridge.PlayerSnapshot snapshot) {
        String clanRole = cleanup(snapshot.clanRole);
        if (!clanRole.isEmpty() && !isNoneValue(clanRole)) {
            return "\u0420\u043e\u043b\u044c: " + clanRole;
        }
        return hasClan(snapshot) ? "\u041a\u043b\u0430\u043d: " + snapshot.clanName : "\u0411\u0435\u0437 \u043a\u043b\u0430\u043d\u0430";
    }

    private static boolean hasClan(ServerLevelBridge.PlayerSnapshot snapshot) {
        String clanName = cleanup(snapshot.clanName);
        return !clanName.isEmpty() && !isNoneValue(clanName);
    }

    private static boolean isAdmin(String adminRole) {
        return adminRole != null && !adminRole.trim().isEmpty() && !"Игрок".equalsIgnoreCase(adminRole.trim());
    }

    private static int getProfessionPriority(ServerLevelBridge.PlayerSnapshot snapshot) {
        String profession = cleanup(snapshot.profession).toLowerCase(Locale.ROOT);
        String professionId = cleanup(snapshot.professionId).toLowerCase(Locale.ROOT);

        if (containsAny(profession, professionId, "police", "cop", "\u043f\u043e\u043b\u0438\u0446", "\u043f\u043e\u043b\u0438\u0446\u0435\u0439")) {
            return 0;
        }
        if (containsAny(profession, professionId, "medic", "doctor", "hospital", "\u043c\u0435\u0434\u0438\u043a", "\u0432\u0440\u0430\u0447")) {
            return 1;
        }
        if (containsAny(profession, professionId, "smith", "blacksmith", "forge", "\u043a\u0443\u0437\u043d", "\u043a\u0443\u0437\u043d\u0435\u0446")) {
            return 2;
        }
        return 10;
    }

    private static boolean containsAny(String profession, String professionId, String... needles) {
        for (String needle : needles) {
            if (profession.contains(needle) || professionId.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static String cleanup(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean isNoneValue(String value) {
        String lowered = cleanup(value).toLowerCase(Locale.ROOT);
        return lowered.isEmpty() || "none".equals(lowered) || "\u043d\u0435\u0442".equals(lowered);
    }

    private static int getAccentColor(ServerLevelBridge.PlayerSnapshot snapshot) {
        return getAccentColor(resolveGroupKey(snapshot.rank) + "|" + snapshot.name);
    }

    private static int getAccentColor(String seed) {
        int hash = seed == null ? 0 : Math.abs(seed.hashCode());
        int bucket = hash % 5;
        if (bucket == 0) {
            return 0xFF2FD3E6;
        }
        if (bucket == 1) {
            return 0xFFFFB454;
        }
        if (bucket == 2) {
            return 0xFFFF70A8;
        }
        if (bucket == 3) {
            return 0xFF79F29A;
        }
        return 0xFFA88BFF;
    }

    private static void drawWholeTexture(Minecraft mc, ResourceLocation texture, int x, int y, int width, int height, float textureWidth, float textureHeight) {
        if (texture == null) {
            return;
        }
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, textureWidth, textureHeight);
    }

    private static ResourceLocation getGroupLabel(ServerLevelBridge.PlayerSnapshot snapshot) {
        String key = resolveGroupKey(snapshot.rank);
        if ("default".equals(key)) {
            return null;
        }
        return new ResourceLocation("krd_mod", "textures/groups/" + key + ".png");
    }

    private static String resolveGroupKey(String rank) {
        String lowered = rank == null ? "" : rank.toLowerCase(Locale.ROOT);
        if (lowered.contains("junior admin")) {
            return "junior_admin";
        }
        if (lowered.contains("admin")) {
            return "admin";
        }
        if (lowered.contains("senior moderator")) {
            return "senior_moderator";
        }
        if (lowered.contains("junior moderator")) {
            return "junior_moderator";
        }
        if (lowered.contains("moderator")) {
            return "moderator";
        }
        if (lowered.contains("senior helper")) {
            return "senior_helper";
        }
        if (lowered.contains("helper")) {
            return "helper";
        }
        if (lowered.contains("legend")) {
            return "legendary";
        }
        if (lowered.contains("vip")) {
            return "vip";
        }
        return "default";
    }

    private static int getRankSourceWidth(ServerLevelBridge.PlayerSnapshot snapshot) {
        String key = resolveGroupKey(snapshot.rank);
        if ("admin".equals(key)) {
            return 83;
        }
        if ("junior_admin".equals(key)) {
            return 120;
        }
        if ("senior_moderator".equals(key)) {
            return 152;
        }
        if ("junior_moderator".equals(key)) {
            return 159;
        }
        if ("moderator".equals(key)) {
            return 123;
        }
        if ("senior_helper".equals(key)) {
            return 112;
        }
        if ("helper".equals(key)) {
            return 85;
        }
        if ("legendary".equals(key)) {
            return 104;
        }
        if ("vip".equals(key)) {
            return 54;
        }
        return 77;
    }

    private static int getRankSourceHeight(ServerLevelBridge.PlayerSnapshot snapshot) {
        String key = resolveGroupKey(snapshot.rank);
        return "legendary".equals(key) ? 22 : 21;
    }

    private static String trimRank(String rank) {
        String cleaned = cleanup(rank);
        if (cleaned.isEmpty()) {
            return "";
        }
        return cleaned.length() > 14 ? cleaned.substring(0, 14) : cleaned;
    }

    private static String compactRank(String rank) {
        String cleaned = cleanup(rank);
        if (cleaned.isEmpty()) {
            return "PLAYER";
        }
        String lowered = cleaned.toLowerCase(Locale.ROOT);
        if (lowered.contains("junior admin")) {
            return "JR.ADMIN";
        }
        if (lowered.contains("admin")) {
            return "ADMIN";
        }
        if (lowered.contains("senior moderator")) {
            return "S.MOD";
        }
        if (lowered.contains("junior moderator")) {
            return "JR.MOD";
        }
        if (lowered.contains("moderator")) {
            return "MOD";
        }
        if (lowered.contains("senior helper")) {
            return "S.HELP";
        }
        if (lowered.contains("helper")) {
            return "HELP";
        }
        if (lowered.contains("legend")) {
            return "LEGEND";
        }
        if (lowered.contains("vip")) {
            return "VIP";
        }
        return trimToLength(cleaned, 8);
    }

    private static String trimToLength(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    private static String trimToWidth(Minecraft mc, String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        if (mc.fontRenderer.getStringWidth(text) <= maxWidth) {
            return text;
        }

        String current = text;
        while (current.length() > 1 && mc.fontRenderer.getStringWidth(current + "...") > maxWidth) {
            current = current.substring(0, current.length() - 1);
        }
        return current + "...";
    }
}
