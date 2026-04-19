package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public final class ServerLevelBridge {
    public static final class PlayerSnapshot {
        public final String name;
        public final String rank;
        public final String profession;
        public final String professionId;
        public final String professionRank;
        public final String adminRole;
        public final String clan;
        public final String clanName;
        public final String clanRole;
        public final String channel;
        public final int level;

        public PlayerSnapshot(String name, String rank, String profession, String professionId, String professionRank,
                              String adminRole, String clan, String clanName, String clanRole, String channel, int level) {
            this.name = name;
            this.rank = rank;
            this.profession = profession;
            this.professionId = professionId;
            this.professionRank = professionRank;
            this.adminRole = adminRole;
            this.clan = clan;
            this.clanName = clanName;
            this.clanRole = clanRole;
            this.channel = channel;
            this.level = level;
        }
    }

    public static final class ChatEntry {
        public final boolean structured;
        public final String channel;
        public final String sender;
        public final String rank;
        public final String profession;
        public final String professionId;
        public final String clan;
        public final String clanName;
        public final String message;
        public final String formattedText;
        public final long timestamp;

        public ChatEntry(boolean structured, String channel, String sender, String rank, String profession, String professionId,
                         String clan, String clanName, String message, String formattedText, long timestamp) {
            this.structured = structured;
            this.channel = channel;
            this.sender = sender;
            this.rank = rank;
            this.profession = profession;
            this.professionId = professionId;
            this.clan = clan;
            this.clanName = clanName;
            this.message = message;
            this.formattedText = formattedText;
            this.timestamp = timestamp;
        }
    }

    private static int cachedLevel = -1;
    private static int cachedProgress = -1;
    private static int cachedStatPoints = -1;
    private static int cachedSkillPoints = -1;
    private static int cachedHp = -1;
    private static int cachedSpeed = -1;
    private static int cachedDamage = -1;
    private static int cachedCurrentXp = -1;
    private static int cachedNextXp = -1;
    private static int cachedRewardStatPoints = -1;
    private static int cachedRewardSkillPoints = -1;
    private static boolean cachedLevelUpReady;
    private static String cachedNextQuest = "";
    private static String cachedNextItems = "";
    private static String cachedLevelStatus = "";
    private static String cachedRank = null;
    private static String cachedProfession = "Нет";
    private static String cachedProfessionRank = "Без ранга";
    private static String cachedProfessionId = "none";
    private static String cachedAdminRole = "";
    private static String cachedClan = "Нет";
    private static String cachedClanName = "";
    private static String cachedClanRole = "Нет роли";
    private static String cachedChannel = "global";
    private static List<String> cachedBreaths = new ArrayList<>();
    private static final Map<String, Integer> cachedSkills = new HashMap<>();
    private static final Map<String, PlayerSnapshot> cachedPlayers = new HashMap<>();
    private static final LinkedList<ChatEntry> cachedChat = new LinkedList<>();
    private static long lastSyncRequestMs = 0L;
    private static boolean syncReceived;
    private static boolean localCacheLoaded;

    private ServerLevelBridge() {
    }

    public static void requestSync(Minecraft mc) {
        ensureLocalCacheLoaded(mc);
        if (mc == null || mc.player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastSyncRequestMs < 500L) {
            return;
        }
        lastSyncRequestMs = now;
        mc.player.sendChatMessage("/levelsync");
    }

    @SubscribeEvent
    public static void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        resetClientCache();
    }

    @SubscribeEvent
    public static void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        resetClientCache();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.world == null) {
            return;
        }
        if (!syncReceived) {
            requestSync(mc);
        }
    }

    public static int getLevel(Minecraft mc) {
        if (cachedLevel >= 0) {
            return cachedLevel;
        }
        int score = getScore(mc, "krd_lvl");
        return score >= 0 ? score : (mc != null && mc.player != null ? mc.player.experienceLevel : 0);
    }

    public static int getProgressPercent(Minecraft mc) {
        if (cachedProgress >= 0) {
            return cachedProgress;
        }
        int score = getScore(mc, "krd_xppct");
        if (score >= 0) {
            return score;
        }
        if (mc == null || mc.player == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, Math.round(mc.player.experience * 100.0F)));
    }

    public static String getRank(Minecraft mc) {
        if (cachedRank != null && !cachedRank.trim().isEmpty()) {
            return cachedRank;
        }
        if (mc == null || mc.player == null || mc.getConnection() == null) {
            return "No rank";
        }

        NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUniqueID());
        if (info == null) {
            return "No rank";
        }

        ITextComponent display = info.getDisplayName();
        String raw = null;
        if (display != null) {
            raw = display.getUnformattedText();
        }
        if (raw == null || raw.trim().isEmpty()) {
            raw = mc.player.getDisplayNameString();
        }
        if (raw == null || raw.trim().isEmpty()) {
            return "No rank";
        }

        String playerName = mc.player.getName();
        String cleaned = TextFormatting.getTextWithoutFormattingCodes(raw);
        if (cleaned == null) {
            cleaned = raw;
        }
        cleaned = cleaned.trim();

        if (cleaned.endsWith(playerName)) {
            cleaned = cleaned.substring(0, cleaned.length() - playerName.length()).trim();
        }

        if (cleaned.endsWith("|")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
        }

        return cleaned.isEmpty() ? "No rank" : cleaned;
    }

    public static String getProfession() {
        return cachedProfession;
    }

    public static String getProfessionId() {
        return cachedProfessionId;
    }

    public static String getProfessionRank() {
        return cachedProfessionRank;
    }

    public static String getClan() {
        return cachedClan;
    }

    public static String getAdminRole() {
        return cachedAdminRole;
    }

    public static String getClanName() {
        return cachedClanName;
    }

    public static String getClanRole() {
        return cachedClanRole;
    }

    public static String getCurrentChannel() {
        return cachedChannel;
    }

    public static int getStatPoints(Minecraft mc) {
        if (cachedStatPoints >= 0) {
            return cachedStatPoints;
        }
        return Math.max(0, getScore(mc, "krd_statp"));
    }

    public static int getSkillPoints(Minecraft mc) {
        ensureLocalCacheLoaded(mc);
        if (cachedSkillPoints >= 0) {
            return cachedSkillPoints;
        }
        return Math.max(0, getScore(mc, "krd_skillp"));
    }

    public static int getHpStat(Minecraft mc) {
        if (cachedHp >= 0) {
            return cachedHp;
        }
        return Math.max(0, getScore(mc, "krd_hpst"));
    }

    public static int getSpeedStat(Minecraft mc) {
        if (cachedSpeed >= 0) {
            return cachedSpeed;
        }
        return Math.max(0, getScore(mc, "krd_spdst"));
    }

    public static int getDamageStat(Minecraft mc) {
        if (cachedDamage >= 0) {
            return cachedDamage;
        }
        return Math.max(0, getScore(mc, "krd_dmgst"));
    }

    public static int getCurrentXp(Minecraft mc) {
        return Math.max(0, cachedCurrentXp);
    }

    public static int getNextLevelXpRequirement(Minecraft mc) {
        return Math.max(0, cachedNextXp);
    }

    public static boolean isLevelUpReady(Minecraft mc) {
        return cachedLevelUpReady;
    }

    public static String getNextLevelQuest(Minecraft mc) {
        return safeValue(cachedNextQuest, "");
    }

    public static String getNextLevelItems(Minecraft mc) {
        return safeValue(cachedNextItems, "");
    }

    public static String getLevelStatus(Minecraft mc) {
        return safeValue(cachedLevelStatus, "");
    }

    public static int getRewardStatPoints(Minecraft mc) {
        return Math.max(0, cachedRewardStatPoints);
    }

    public static int getRewardSkillPoints(Minecraft mc) {
        return Math.max(0, cachedRewardSkillPoints);
    }

    public static boolean hasBreath(Minecraft mc, String breathId) {
        ensureLocalCacheLoaded(mc);
        String normalized = breathId == null ? "" : breathId.toLowerCase();
        if ("voda".equals(normalized)) {
            return !cachedBreaths.isEmpty() ? cachedBreaths.contains("voda") : getScore(mc, "krd_bvoda") > 0;
        }
        if ("grom".equals(normalized)) {
            return !cachedBreaths.isEmpty() ? cachedBreaths.contains("grom") : getScore(mc, "krd_bgrom") > 0;
        }
        if ("veter".equals(normalized)) {
            return !cachedBreaths.isEmpty() ? cachedBreaths.contains("veter") : getScore(mc, "krd_bveter") > 0;
        }
        if ("zmei".equals(normalized)) {
            return !cachedBreaths.isEmpty() ? cachedBreaths.contains("zmei") : getScore(mc, "krd_bzmei") > 0;
        }
        if ("plamya".equals(normalized)) {
            return !cachedBreaths.isEmpty() ? cachedBreaths.contains("plamya") : getScore(mc, "krd_bplamya") > 0;
        }
        return false;
    }

    public static int getSkillLevel(Minecraft mc, String skillId) {
        ensureLocalCacheLoaded(mc);
        Integer cached = cachedSkills.get(skillId.toLowerCase());
        if (cached != null) {
            return cached;
        }
        return Math.max(0, getScore(mc, "krd_" + skillId.toLowerCase()));
    }

    public static List<String> getOwnedBreaths(Minecraft mc) {
        ensureLocalCacheLoaded(mc);
        if (!cachedBreaths.isEmpty()) {
            return new ArrayList<>(cachedBreaths);
        }
        ArrayList<String> result = new ArrayList<>();
        if (hasBreath(mc, "voda")) {
            result.add("voda");
        }
        if (hasBreath(mc, "grom")) {
            result.add("grom");
        }
        if (hasBreath(mc, "veter")) {
            result.add("veter");
        }
        if (hasBreath(mc, "zmei")) {
            result.add("zmei");
        }
        if (hasBreath(mc, "plamya")) {
            result.add("plamya");
        }
        return result;
    }

    public static List<String> getAvailableSkills(Minecraft mc, String breathId) {
        List<String> all = getAllSkillsForBreath(breathId);
        ArrayList<String> result = new ArrayList<>();
        for (String skillId : all) {
            if (getSkillLevel(mc, skillId) > 0) {
                result.add(skillId);
            }
        }
        return result;
    }

    public static List<String> getAllSkillsForBreath(String breathId) {
        if ("grom".equalsIgnoreCase(breathId)) {
            return Arrays.asList("lk1", "lk2", "lk3", "lk4", "lk5", "lk6", "lk7");
        }
        if ("voda".equalsIgnoreCase(breathId)) {
            return Arrays.asList("wk1", "wk2", "wk3", "wk4", "wk6", "wk8", "wk9", "wk10", "wk11");
        }
        if ("veter".equalsIgnoreCase(breathId)) {
            return Arrays.asList("vk1", "vk2", "vk3", "vk4", "vk5", "vk7", "vk8");
        }
        if ("zmei".equalsIgnoreCase(breathId)) {
            return Arrays.asList("zk1", "zk2", "zk3", "zk4", "zk5");
        }
        if ("plamya".equalsIgnoreCase(breathId)) {
            return Arrays.asList("fk1", "fk2", "fk3", "fk4", "fk5", "fk9");
        }
        return Collections.emptyList();
    }

    public static void applyLocalSkillUpgrade(String skillId) {
        if (skillId == null || skillId.trim().isEmpty()) {
            return;
        }
        String normalized = skillId.toLowerCase();
        int current = cachedSkills.containsKey(normalized) ? cachedSkills.get(normalized) : 0;
        if (current < 3) {
            cachedSkills.put(normalized, current + 1);
        }
        if (cachedSkillPoints > 0) {
            cachedSkillPoints = Math.max(0, cachedSkillPoints - 1);
        }
        syncReceived = false;
        lastSyncRequestMs = 0L;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            persistLocalCache(mc);
        }
    }

    public static List<PlayerSnapshot> getOnlineSnapshots(Minecraft mc) {
        ArrayList<PlayerSnapshot> result = new ArrayList<>();
        if (mc != null && mc.getConnection() != null) {
            ArrayList<String> onlineNames = new ArrayList<>();
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                String name = info.getGameProfile().getName();
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }
                onlineNames.add(name);
                PlayerSnapshot cached = cachedPlayers.get(name);
                if (cached != null) {
                    result.add(cached);
                } else {
                    result.add(new PlayerSnapshot(name, "", "", "none", "", "", "", "", "", "", -1));
                }
            }
            cachedPlayers.keySet().retainAll(onlineNames);
        }
        result.sort(Comparator.comparing(snapshot -> snapshot.name.toLowerCase()));
        return result;
    }

    public static List<ChatEntry> getChatEntries() {
        return new ArrayList<>(cachedChat);
    }

    @SubscribeEvent
    public static void onChat(ClientChatReceivedEvent event) {
        if (event.getMessage() == null) {
            return;
        }
        String raw = event.getMessage().getUnformattedText();
        if (raw == null) {
            return;
        }

        int syncIndex = raw.indexOf("[KRD_SYNC]");
        if (syncIndex >= 0) {
            event.setCanceled(true);
            parseSyncPayload(raw.substring(syncIndex + "[KRD_SYNC]".length()));
            return;
        }

        int playerIndex = raw.indexOf("[KRD_PLAYER]");
        if (playerIndex >= 0) {
            event.setCanceled(true);
            parsePlayerPayload(raw.substring(playerIndex + "[KRD_PLAYER]".length()));
            return;
        }

        int chatIndex = raw.indexOf("[KRD_CHAT]");
        if (chatIndex >= 0) {
            event.setCanceled(true);
            parseChatPayload(raw.substring(chatIndex + "[KRD_CHAT]".length()));
            return;
        }

        cachedChat.add(new ChatEntry(false, "system", "", "", "", "none", "", "", "", event.getMessage().getFormattedText(), System.currentTimeMillis()));
        trimChat();
        event.setCanceled(true);
    }

    private static void parseSyncPayload(String payload) {
        Map<String, String> values = parseValues(payload);
        syncReceived = true;
        cachedLevel = parseInt(values.get("lvl"));
        cachedProgress = parseInt(values.get("xp"));
        cachedRank = values.get("rank");
        cachedStatPoints = parseInt(values.get("statp"));
        cachedSkillPoints = parseInt(values.get("skillp"));
        cachedHp = parseInt(values.get("hp"));
        cachedSpeed = parseInt(values.get("spd"));
        cachedDamage = parseInt(values.get("dmg"));
        cachedCurrentXp = parseInt(values.get("xpv"));
        cachedNextXp = parseInt(values.get("nextxp"));
        cachedRewardStatPoints = parseInt(values.get("rstat"));
        cachedRewardSkillPoints = parseInt(values.get("rskill"));
        cachedLevelUpReady = parseInt(values.get("ready")) > 0;
        cachedNextQuest = safeValue(values.get("quest"), "");
        cachedNextItems = safeValue(values.get("items"), "");
        cachedLevelStatus = safeValue(values.get("lvlstatus"), "");
        cachedProfession = safeValue(values.get("profession"), "Нет");
        cachedProfessionRank = safeValue(values.get("profession_rank"), "Без ранга");
        cachedProfessionId = safeValue(values.get("profession_id"), "none");
        cachedAdminRole = safeValue(values.get("admin_role"), "");
        cachedClan = safeValue(values.get("clan"), "Нет");
        cachedClanName = safeValue(values.get("clan_name"), "");
        cachedClanRole = safeValue(values.get("clan_role"), "Нет роли");
        cachedChannel = safeValue(values.get("channel"), "global");

        cachedBreaths.clear();
        String breaths = values.get("breaths");
        if (breaths != null && !breaths.trim().isEmpty()) {
            cachedBreaths.addAll(Arrays.asList(breaths.split(",")));
        }

        cachedSkills.clear();
        String skills = values.get("skills");
        if (skills != null && !skills.trim().isEmpty()) {
            for (String entry : skills.split(",")) {
                String[] split = entry.split(":");
                if (split.length == 2) {
                    cachedSkills.put(split[0].toLowerCase(), parseInt(split[1]));
                }
            }
        }
        persistLocalCache(Minecraft.getMinecraft());
    }

    private static void parsePlayerPayload(String payload) {
        Map<String, String> values = parseValues(payload);
        String name = safeValue(values.get("name"), "");
        if (name.isEmpty()) {
            return;
        }

        cachedPlayers.put(name, new PlayerSnapshot(
                name,
                safeValue(values.get("rank"), ""),
                safeValue(values.get("profession"), "Нет"),
                safeValue(values.get("profession_id"), "none"),
                safeValue(values.get("profession_rank"), "Без ранга"),
                safeValue(values.get("admin_role"), ""),
                safeValue(values.get("clan"), "Нет"),
                safeValue(values.get("clan_name"), ""),
                safeValue(values.get("clan_role"), "Нет роли"),
                safeValue(values.get("channel"), ""),
                parseInt(values.get("level"))
        ));
    }

    private static void parseChatPayload(String payload) {
        Map<String, String> values = parseValues(payload);
        cachedChat.add(new ChatEntry(
                true,
                safeValue(values.get("channel"), "global"),
                safeValue(values.get("sender"), "Unknown"),
                safeValue(values.get("rank"), ""),
                safeValue(values.get("profession"), "Нет"),
                safeValue(values.get("profession_id"), "none"),
                safeValue(values.get("clan"), "Нет"),
                safeValue(values.get("clan_name"), ""),
                safeValue(values.get("message"), ""),
                "",
                System.currentTimeMillis()
        ));
        trimChat();
    }

    private static void trimChat() {
        while (cachedChat.size() > 200) {
            cachedChat.removeFirst();
        }
    }

    private static Map<String, String> parseValues(String payload) {
        Map<String, String> values = new HashMap<>();
        for (String part : payload.split(";")) {
            int idx = part.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String key = part.substring(0, idx);
            String value = part.substring(idx + 1);
            if ("breaths".equals(key) || "skills".equals(key) || "lvl".equals(key) || "xp".equals(key)
                    || "statp".equals(key) || "skillp".equals(key) || "hp".equals(key) || "spd".equals(key)
                    || "dmg".equals(key) || "reg".equals(key) || "jmp".equals(key) || "luk".equals(key)
                    || "xpv".equals(key) || "nextxp".equals(key) || "rstat".equals(key) || "rskill".equals(key)
                    || "ready".equals(key) || "level".equals(key)) {
                values.put(key, value);
            } else {
                values.put(key, decodeValue(value));
            }
        }
        return values;
    }

    private static String decodeValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            return value;
        }
    }

    private static String safeValue(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private static int getScore(Minecraft mc, String objectiveName) {
        if (mc == null || mc.player == null || mc.world == null) {
            return -1;
        }
        Scoreboard scoreboard = mc.world.getScoreboard();
        if (scoreboard == null) {
            return -1;
        }
        ScoreObjective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            return -1;
        }
        Score score = scoreboard.getOrCreateScore(mc.player.getName(), objective);
        return score == null ? -1 : score.getScorePoints();
    }

    private static void resetClientCache() {
        cachedLevel = -1;
        cachedProgress = -1;
        cachedStatPoints = -1;
        cachedSkillPoints = -1;
        cachedHp = -1;
        cachedSpeed = -1;
        cachedDamage = -1;
        cachedCurrentXp = -1;
        cachedNextXp = -1;
        cachedRewardStatPoints = -1;
        cachedRewardSkillPoints = -1;
        cachedLevelUpReady = false;
        cachedNextQuest = "";
        cachedNextItems = "";
        cachedLevelStatus = "";
        cachedRank = null;
        cachedProfession = "Нет";
        cachedProfessionRank = "Без ранга";
        cachedProfessionId = "none";
        cachedAdminRole = "";
        cachedClan = "Нет";
        cachedClanName = "";
        cachedClanRole = "Нет роли";
        cachedChannel = "global";
        cachedBreaths = new ArrayList<>();
        cachedSkills.clear();
        cachedPlayers.clear();
        cachedChat.clear();
        lastSyncRequestMs = 0L;
        syncReceived = false;
        localCacheLoaded = false;
    }

    private static void ensureLocalCacheLoaded(Minecraft mc) {
        if (localCacheLoaded || mc == null) {
            return;
        }
        localCacheLoaded = true;

        File file = getClientCacheFile(mc);
        if (!file.isFile()) {
            return;
        }

        Properties properties = new Properties();
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            properties.load(input);
            cachedBreaths = new ArrayList<>();
            String breaths = properties.getProperty("breaths", "");
            if (!breaths.trim().isEmpty()) {
                cachedBreaths.addAll(Arrays.asList(breaths.split(",")));
            }

            cachedSkills.clear();
            String skills = properties.getProperty("skills", "");
            if (!skills.trim().isEmpty()) {
                for (String entry : skills.split(",")) {
                    String[] split = entry.split(":");
                    if (split.length == 2) {
                        cachedSkills.put(split[0].toLowerCase(), parseInt(split[1]));
                    }
                }
            }

            cachedSkillPoints = parseInt(properties.getProperty("skill_points", ""));
        } catch (IOException ignored) {
        }
    }

    private static void persistLocalCache(Minecraft mc) {
        if (mc == null) {
            return;
        }

        File file = getClientCacheFile(mc);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Properties properties = new Properties();
        properties.setProperty("breaths", String.join(",", cachedBreaths));
        ArrayList<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cachedSkills.entrySet()) {
            parts.add(entry.getKey() + ":" + entry.getValue());
        }
        Collections.sort(parts);
        properties.setProperty("skills", String.join(",", parts));
        properties.setProperty("skill_points", Integer.toString(cachedSkillPoints));

        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
            properties.store(output, "KRD cached sync");
        } catch (IOException ignored) {
        }
    }

    private static File getClientCacheFile(Minecraft mc) {
        return new File(mc.mcDataDir, "config/krd_client_sync.properties");
    }
}
