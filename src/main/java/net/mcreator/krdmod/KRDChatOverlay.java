package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public class KRDChatOverlay extends Gui {
    private static final String[] TAB_IDS = new String[]{"all", "chat", "trade", "system"};
    private static final String[] TAB_LABELS = new String[]{"All", "Chat", "Trade", "System"};
    private static final int[] TAB_COLORS = new int[]{0xFFE9A93B, 0xFF4F7BFF, 0xFF3DBE5A, 0xFF9B59B6};

    private static final int CHAT_WIDTH = 286;
    private static final int EXPANDED_HEIGHT = 156;
    private static final int COMPACT_HEIGHT = 76;
    private static final int LINE_HEIGHT = 12;
    private static final int TAB_HEIGHT = 16;
    private static final int INPUT_HEIGHT = 18;
    private static final int TAB_GAP = 3;
    private static final int SIDE_PADDING = 8;
    private static final int COMPACT_BOTTOM_MARGIN = 46;
    private static final int EXPANDED_BOTTOM_MARGIN = 10;
    private static final int AVATAR_SIZE = 12;
    private static final int AVATAR_GAP = 5;
    private static final int PANEL_BG_EXPANDED = 0xC0121922;
    private static final int PANEL_BG_COMPACT = 0x8F101720;
    private static final int PANEL_EDGE = 0x6632D6C8;
    private static final int PANEL_LINE = 0x2232E0FF;
    private static final int TEXT_PRIMARY = 0xFFF4F8FB;
    private static final int TEXT_SECONDARY = 0xFFB5C6D4;
    private static final int TEXT_MUTED = 0xFF8EA6B7;
    private static int scrollOffset = 0;
    private static String selectedTab = "chat";
    private static final List<TabBounds> lastTabBounds = new ArrayList<>();

    private static final class TabBounds {
        private final String id;
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;

        private TabBounds(String id, int x1, int y1, int x2, int y2) {
            this.id = id;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        private boolean contains(int mouseX, int mouseY) {
            return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
        }
    }

    private static final class RenderRow {
        private static final int HEADER = 0;
        private static final int BODY = 1;
        private static final int SYSTEM = 2;

        private final int type;
        private final ServerLevelBridge.ChatEntry entry;
        private final String text;

        private RenderRow(int type, ServerLevelBridge.ChatEntry entry, String text) {
            this.type = type;
            this.entry = entry;
            this.text = text;
        }
    }

    @SubscribeEvent
    public static void hideVanillaChat(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void handleMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiChat)) {
            scrollOffset = 0;
            return;
        }

        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            List<RenderRow> rows = buildVisibleRows(mc, true);
            int viewportLines = getExpandedViewportLines(mc);
            int maxScroll = Math.max(0, rows.size() - viewportLines);
            if (wheel > 0) {
                scrollOffset = Math.min(maxScroll, scrollOffset + 2);
            } else {
                scrollOffset = Math.max(0, scrollOffset - 2);
            }
            event.setCanceled(true);
            return;
        }

        if (!Mouse.getEventButtonState() || Mouse.getEventButton() != 0) {
            return;
        }

        if (handleTabClick(mc)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderCustomChat(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.fontRenderer == null) {
            return;
        }
        KRDChatSettings.ensureLoaded(mc);
        float widthScale = KRDChatSettings.getWidthScale(mc);
        float heightScale = KRDChatSettings.getHeightScale(mc);
        float uiScale = KRDChatSettings.getUiScale(mc);
        float compactOpacity = KRDChatSettings.getCompactOpacity(mc);
        float expandedOpacity = KRDChatSettings.getExpandedOpacity(mc);

        boolean expanded = mc.currentScreen instanceof GuiChat;
        List<RenderRow> rows = buildVisibleRows(mc, expanded);
        if (rows.isEmpty() && !expanded) {
            lastTabBounds.clear();
            return;
        }

        ScaledResolution resolution = event.getResolution();
        int width = Math.min(getScaledWidth(CHAT_WIDTH, widthScale, uiScale), resolution.getScaledWidth() - 20);
        int height = expanded
                ? getScaledHeight(EXPANDED_HEIGHT, heightScale, uiScale)
                : getScaledHeight(COMPACT_HEIGHT, heightScale, uiScale);
        int tabHeight = getScaledHeight(TAB_HEIGHT, heightScale, uiScale);
        int inputHeight = getScaledHeight(INPUT_HEIGHT, heightScale, uiScale);
        int sidePadding = Math.max(4, getScaledWidth(SIDE_PADDING, widthScale, uiScale) - 3);
        int avatarSize = Math.max(10, getScaledHeight(AVATAR_SIZE, heightScale, uiScale));
        int avatarGap = Math.max(3, getScaledWidth(AVATAR_GAP, widthScale, uiScale));
        int lineHeight = Math.max(Math.max(10, getScaledHeight(LINE_HEIGHT, heightScale, uiScale)), avatarSize + 3);
        int x = 2;
        int y = resolution.getScaledHeight() - height - (expanded ? EXPANDED_BOTTOM_MARGIN : COMPACT_BOTTOM_MARGIN);
        int contentTop = y + (expanded ? tabHeight + 7 : 6);
        int contentBottom = y + height - (expanded ? inputHeight + 8 : 6);
        int contentHeight = Math.max(lineHeight, contentBottom - contentTop);
        int viewportLines = Math.max(1, contentHeight / lineHeight);

        int maxScroll = Math.max(0, rows.size() - viewportLines);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        int end = rows.size() - scrollOffset;
        int start = Math.max(0, end - viewportLines);

        drawRect(x + 4, y + 4, x + width + 4, y + height + 4, 0x22000000);
        drawRect(x, y, x + width, y + height, applyOpacity(expanded ? PANEL_BG_EXPANDED : PANEL_BG_COMPACT, expanded ? expandedOpacity : compactOpacity));
        drawRect(x, y, x + width, y + 1, PANEL_EDGE);
        drawRect(x, y, x + 1, y + height, PANEL_EDGE);
        drawRect(x + width - 1, y, x + width, y + height, 0x22000000);
        drawRect(x, y + height - 1, x + width, y + height, 0x22000000);

        if (expanded) {
            drawTabs(mc, x + 6, y + 4, width - 14, tabHeight);
        } else {
            lastTabBounds.clear();
        }

        int drawY = contentBottom - lineHeight + 1;
        for (int i = end - 1; i >= start; i--) {
            RenderRow row = rows.get(i);
            if (row.type == RenderRow.HEADER) {
                drawRect(x + 4, drawY - 2, x + width - 7, drawY + lineHeight - 2, expanded ? 0x22334554 : 0x182B3947);
            } else if (expanded) {
                drawRect(x + 4, drawY - 1, x + width - 7, drawY + lineHeight - 3, row.type == RenderRow.SYSTEM ? 0x18161E26 : 0x10000000);
            }
            drawRow(mc, row, x + 2, drawY, width - 6, avatarSize, avatarGap);
            drawY -= lineHeight;
        }

        if (expanded) {
            drawScrollBar(x + width - 6, contentTop, contentHeight, rows.size(), viewportLines);
            drawRect(x + 4, y + height - inputHeight - 3, x + width - 6, y + height - 4, 0x22000000);
            drawRect(x + 4, y + height - inputHeight - 3, x + width - 6, y + height - inputHeight - 2, 0x22FFFFFF);
        } else {
            drawRect(x + 4, y + 4, x + width - 6, y + 5, PANEL_LINE);
        }
    }

    private static boolean handleTabClick(Minecraft mc) {
        if (lastTabBounds.isEmpty()) {
            return false;
        }

        ScaledResolution resolution = new ScaledResolution(mc);
        int mouseX = Mouse.getEventX() * resolution.getScaledWidth() / mc.displayWidth;
        int mouseY = resolution.getScaledHeight() - Mouse.getEventY() * resolution.getScaledHeight() / mc.displayHeight - 1;

        for (TabBounds bounds : lastTabBounds) {
            if (bounds.contains(mouseX, mouseY)) {
                selectedTab = bounds.id;
                scrollOffset = 0;
                return true;
            }
        }
        return false;
    }

    private static void drawTabs(Minecraft mc, int x, int y, int width, int tabHeight) {
        lastTabBounds.clear();
        int tabWidth = Math.max(34, (width - TAB_GAP * (TAB_IDS.length - 1)) / TAB_IDS.length);

        for (int i = 0; i < TAB_IDS.length; i++) {
            int tabX = x + i * (tabWidth + TAB_GAP);
            boolean active = TAB_IDS[i].equals(selectedTab);
            int fill = active ? TAB_COLORS[i] : 0x55303A42;
            int textColor = active ? 0xFFFFFFFF : 0xFFD6DEE4;

            drawRect(tabX, y, tabX + tabWidth, y + tabHeight, fill);
            if (active) {
                drawRect(tabX, y + tabHeight - 2, tabX + tabWidth, y + tabHeight, 0xFFFFFFFF);
            }

            String label = TAB_LABELS[i];
            int labelX = tabX + (tabWidth - mc.fontRenderer.getStringWidth(label)) / 2;
            mc.fontRenderer.drawStringWithShadow(label, labelX, y + 3, textColor);
            lastTabBounds.add(new TabBounds(TAB_IDS[i], tabX, y, tabX + tabWidth, y + tabHeight));
        }
    }

    private static List<RenderRow> buildVisibleRows(Minecraft mc, boolean expanded) {
        List<ServerLevelBridge.ChatEntry> entries = ServerLevelBridge.getChatEntries();
        List<RenderRow> rows = new ArrayList<>();
        float widthScale = KRDChatSettings.getWidthScale(mc);
        float uiScale = KRDChatSettings.getUiScale(mc);
        int bodyWrapWidth = Math.max(80, getScaledWidth(CHAT_WIDTH - SIDE_PADDING * 2 - 10, widthScale, uiScale));

        for (ServerLevelBridge.ChatEntry entry : entries) {
            if (!expanded && System.currentTimeMillis() - entry.timestamp > 25000L) {
                continue;
            }

            if (entry.structured) {
                if (!shouldShowStructured(entry.channel)) {
                    continue;
                }
                rows.add(new RenderRow(RenderRow.HEADER, entry, ""));
                List<String> wrappedBody = mc.fontRenderer.listFormattedStringToWidth(entry.message, bodyWrapWidth);
                for (String bodyLine : wrappedBody) {
                    rows.add(new RenderRow(RenderRow.BODY, entry, bodyLine));
                }
            } else if (shouldShowSystem()) {
                List<String> wrappedSystem = mc.fontRenderer.listFormattedStringToWidth(entry.formattedText, getScaledWidth(CHAT_WIDTH - SIDE_PADDING * 2 - 8, widthScale, uiScale));
                for (String systemLine : wrappedSystem) {
                    rows.add(new RenderRow(RenderRow.SYSTEM, entry, systemLine));
                }
            }
        }

        return rows;
    }

    private static void drawRow(Minecraft mc, RenderRow row, int x, int y, int width, int avatarSize, int avatarGap) {
        if (row.type == RenderRow.HEADER) {
            drawStructuredHeader(mc, row.entry, x, y, width, avatarSize, avatarGap);
            return;
        }
        if (row.type == RenderRow.BODY) {
            mc.fontRenderer.drawStringWithShadow("§f" + row.text, x, y, TEXT_PRIMARY);
            return;
        }
        mc.fontRenderer.drawStringWithShadow(row.text, x, y, TEXT_SECONDARY);
    }

    private static void drawStructuredHeader(Minecraft mc, ServerLevelBridge.ChatEntry entry, int x, int y, int width, int avatarSize, int avatarGap) {
        KRDTabOverlay.drawPlayerAvatar(mc, entry.sender, x, y - 2, avatarSize);

        int cursorX = x + avatarSize + 2;
        int rightLimit = x + width - 6;
        String sender = trimToWidth(mc, entry.sender, Math.max(44, width / 4));
        mc.fontRenderer.drawStringWithShadow(sender, cursorX, y, TEXT_PRIMARY);
        cursorX += mc.fontRenderer.getStringWidth(sender) + 4;

        cursorX = drawSmallIcon(mc, cursorX, y, getChannelLabel(entry.channel), getChannelColor(entry.channel), 0xFFFFFFFF);
        cursorX += 2;

        if (entry.clanName != null && !entry.clanName.trim().isEmpty() && !"Нет".equalsIgnoreCase(entry.clanName)) {
            String clanText = trimToWidth(mc, entry.clanName, Math.max(28, (rightLimit - cursorX) / 2));
            cursorX = drawChip(mc, cursorX + 2, y, clanText, 0xCC214D52, 0xFFE3FDFF);
        }

        if (entry.rank != null && !entry.rank.trim().isEmpty()) {
            int rankWidth = Math.max(32, rightLimit - (cursorX + 2));
            String rankText = trimToWidth(mc, safeUpper(entry.rank), rankWidth - 10);
            drawChip(mc, cursorX + 2, y, rankText, 0x884E5668, 0xFFFFFFFF);
        }
    }

    private static int drawSmallIcon(Minecraft mc, int x, int y, String text, int fill, int textColor) {
        int iconWidth = 9 + mc.fontRenderer.getStringWidth(text);
        drawRect(x, y - 1, x + iconWidth, y + 8, fill);
        mc.fontRenderer.drawStringWithShadow(text, x + 3, y, textColor);
        return x + iconWidth;
    }

    private static int drawChip(Minecraft mc, int x, int y, String text, int fill, int textColor) {
        int chipWidth = 10 + mc.fontRenderer.getStringWidth(text);
        drawRect(x, y - 1, x + chipWidth, y + 8, fill);
        mc.fontRenderer.drawStringWithShadow(text, x + 4, y, textColor);
        return x + chipWidth;
    }

    private static boolean shouldShowStructured(String entryChannel) {
        if ("system".equals(selectedTab)) {
            return false;
        }
        if ("all".equals(selectedTab)) {
            return true;
        }
        if ("trade".equals(selectedTab)) {
            return "trade".equalsIgnoreCase(entryChannel);
        }
        return "global".equalsIgnoreCase(entryChannel) || "local".equalsIgnoreCase(entryChannel);
    }

    private static boolean shouldShowSystem() {
        return "all".equals(selectedTab) || "system".equals(selectedTab);
    }

    private static String getChannelLabel(String channel) {
        if ("local".equalsIgnoreCase(channel)) {
            return "L";
        }
        if ("trade".equalsIgnoreCase(channel)) {
            return "T";
        }
        if ("all".equalsIgnoreCase(channel)) {
            return "A";
        }
        return "G";
    }

    private static int getChannelColor(String channel) {
        if ("local".equalsIgnoreCase(channel)) {
            return 0xFF4F7BFF;
        }
        if ("trade".equalsIgnoreCase(channel)) {
            return 0xFF3DBE5A;
        }
        return 0xFFE84393;
    }

    private static String safeUpper(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }

    private static int getExpandedViewportLines(Minecraft mc) {
        float heightScale = KRDChatSettings.getHeightScale(mc);
        float uiScale = KRDChatSettings.getUiScale(mc);
        return Math.max(1, (getScaledHeight(EXPANDED_HEIGHT, heightScale, uiScale)
                - getScaledHeight(TAB_HEIGHT, heightScale, uiScale)
                - getScaledHeight(INPUT_HEIGHT, heightScale, uiScale) - 20)
                / Math.max(10, getScaledHeight(LINE_HEIGHT, heightScale, uiScale)));
    }

    private static void drawScrollBar(int x, int y, int height, int totalLines, int viewportLines) {
        drawRect(x, y, x + 3, y + height, 0x33000000);
        if (totalLines <= viewportLines) {
            return;
        }

        int thumbHeight = Math.max(14, height * viewportLines / totalLines);
        int maxOffset = height - thumbHeight;
        int maxScroll = Math.max(1, totalLines - viewportLines);
        int thumbY = y + maxOffset - (maxOffset * scrollOffset / maxScroll);
        drawRect(x, thumbY, x + 3, thumbY + thumbHeight, 0xFF29D6C8);
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

    private static int getScaledWidth(int base, float scaleX, float scaleUi) {
        return Math.max(40, Math.round(base * scaleX * scaleUi));
    }

    private static int getScaledHeight(int base, float scaleY, float scaleUi) {
        return Math.max(10, Math.round(base * scaleY * scaleUi));
    }

    private static int applyOpacity(int color, float opacity) {
        int alpha = (color >>> 24) & 0xFF;
        int rgb = color & 0x00FFFFFF;
        int adjustedAlpha = Math.max(0, Math.min(255, Math.round(alpha * opacity)));
        return (adjustedAlpha << 24) | rgb;
    }

}
