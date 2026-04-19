package net.mcreator.krdmod.trade;

import net.mcreator.krdmod.KrdModMod;
import net.mcreator.krdmod.trade.network.OpenTradeGuiMessage;
import net.mcreator.krdmod.trade.network.TradeActionMessage;
import net.mcreator.krdmod.trade.network.TradeStateMessage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TradeManager {
	private static final long REQUEST_COOLDOWN_MS = 2500L;
	private static final long DUPLICATE_HINT_COOLDOWN_MS = 1500L;

	public static final TradeManager INSTANCE = new TradeManager();

	private final Map<UUID, Set<UUID>> pendingRequests = new HashMap<>();
	private final Map<UUID, TradeSession> sessionsByPlayer = new HashMap<>();
	private final Map<UUID, Long> requesterCooldowns = new HashMap<>();
	private final Map<String, Long> pendingRequestTimes = new HashMap<>();
	private final Map<String, Long> duplicateHintTimes = new HashMap<>();

	private TradeManager() {
	}

	public void requestTrade(EntityPlayerMP requester, EntityPlayerMP target) {
		long now = System.currentTimeMillis();
		if (requester.getUniqueID().equals(target.getUniqueID())) {
			requester.sendMessage(new TextComponentString("Нельзя отправить трейд самому себе."));
			return;
		}
		if (sessionsByPlayer.containsKey(requester.getUniqueID()) || sessionsByPlayer.containsKey(target.getUniqueID())) {
			requester.sendMessage(new TextComponentString("Один из игроков уже находится в трейде."));
			return;
		}
		if (requesterCooldowns.getOrDefault(requester.getUniqueID(), 0L) > now) {
			long waitMillis = requesterCooldowns.get(requester.getUniqueID()) - now;
			requester.sendMessage(new TextComponentString("Слишком часто. Подождите " + Math.max(1L, (waitMillis + 999L) / 1000L) + " сек."));
			return;
		}

		Set<UUID> pendingForRequester = pendingRequests.get(requester.getUniqueID());
		if (pendingForRequester != null && pendingForRequester.remove(target.getUniqueID())) {
			clearPending(requester.getUniqueID(), target.getUniqueID());
			startTrade(requester, target);
			return;
		}

		String pairKey = pairKey(requester.getUniqueID(), target.getUniqueID());
		if (pendingRequestTimes.containsKey(pairKey)) {
			if (duplicateHintTimes.getOrDefault(pairKey, 0L) <= now) {
				requester.sendMessage(new TextComponentString(
						"Запрос уже отправлен. Игрок должен посмотреть на вас и нажать клавишу трейда."));
				duplicateHintTimes.put(pairKey, now + DUPLICATE_HINT_COOLDOWN_MS);
			}
			return;
		}

		pendingRequests.computeIfAbsent(target.getUniqueID(), key -> new HashSet<>()).add(requester.getUniqueID());
		pendingRequestTimes.put(pairKey, now);
		requesterCooldowns.put(requester.getUniqueID(), now + REQUEST_COOLDOWN_MS);

		requester.sendMessage(new TextComponentString("Запрос на трейд отправлен игроку " + target.getName() + "."));
		target.sendMessage(new TextComponentString(
				"Игрок " + requester.getName() + " предлагает обмен. Посмотрите на него и нажмите клавишу трейда."));
	}

	public void handleAction(EntityPlayerMP player, int action) {
		TradeSession session = sessionsByPlayer.get(player.getUniqueID());
		if (session == null) {
			return;
		}
		if (action == TradeActionMessage.ACTION_TOGGLE_READY) {
			session.toggleReady(player.getUniqueID());
		} else if (action == TradeActionMessage.ACTION_CANCEL) {
			session.cancel(player);
		}
	}

	public void syncSessionState(TradeSession session) {
		for (Map.Entry<UUID, TradeSession> entry : sessionsByPlayer.entrySet()) {
			if (entry.getValue() != session) {
				continue;
			}
			EntityPlayerMP player = getPlayer(entry.getKey());
			if (player == null || !(player.openContainer instanceof TradeContainer)) {
				continue;
			}
			KrdModMod.PACKET_HANDLER.sendTo(new TradeStateMessage(player.openContainer.windowId,
					session.isOwnReady(player.getUniqueID()), session.isOtherReady(player.getUniqueID()), session.getCountdownTicks()), player);
			player.openContainer.detectAndSendChanges();
		}
	}

	public void finishSession(TradeSession session) {
		sessionsByPlayer.entrySet().removeIf(entry -> entry.getValue() == session);
	}

	public EntityPlayerMP getPlayer(UUID playerId) {
		if (playerId == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
			return null;
		}
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerId);
	}

	private void startTrade(EntityPlayerMP first, EntityPlayerMP second) {
		clearPending(first.getUniqueID(), second.getUniqueID());
		TradeSession session = new TradeSession(first.getUniqueID(), second.getUniqueID());
		sessionsByPlayer.put(first.getUniqueID(), session);
		sessionsByPlayer.put(second.getUniqueID(), session);
		openTrade(first, session, second.getName());
		openTrade(second, session, first.getName());
		syncSessionState(session);
	}

	private void openTrade(EntityPlayerMP player, TradeSession session, String otherName) {
		player.getNextWindowId();
		player.closeContainer();
		int windowId = player.currentWindowId;
		TradeContainer container = new TradeContainer(player, session, otherName);
		container.windowId = windowId;
		player.openContainer = container;
		container.addListener(player);
		KrdModMod.PACKET_HANDLER.sendTo(new OpenTradeGuiMessage(otherName, windowId), player);
		MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, container));
	}

	private void clearPending(UUID first, UUID second) {
		Set<UUID> firstSet = pendingRequests.get(first);
		if (firstSet != null) {
			firstSet.remove(second);
		}
		Set<UUID> secondSet = pendingRequests.get(second);
		if (secondSet != null) {
			secondSet.remove(first);
		}
		String pairKey = pairKey(first, second);
		pendingRequestTimes.remove(pairKey);
		duplicateHintTimes.remove(pairKey);
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (!(event.player instanceof EntityPlayerMP)) {
			return;
		}
		TradeSession session = sessionsByPlayer.get(event.player.getUniqueID());
		if (session != null) {
			session.onContainerClosed((EntityPlayerMP) event.player);
		}
		pendingRequests.remove(event.player.getUniqueID());
		pendingRequests.values().forEach(requests -> requests.remove(event.player.getUniqueID()));
		clearRequestsFor(event.player.getUniqueID());
	}

	@SubscribeEvent
	public void onContainerClose(PlayerContainerEvent.Close event) {
		Container container = event.getContainer();
		if (container instanceof TradeContainer && event.getEntityPlayer() instanceof EntityPlayerMP) {
			TradeSession session = sessionsByPlayer.get(event.getEntityPlayer().getUniqueID());
			if (session != null) {
				session.onContainerClosed((EntityPlayerMP) event.getEntityPlayer());
			}
		}
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Set<TradeSession> sessions = new HashSet<>(sessionsByPlayer.values());
		for (TradeSession session : sessions) {
			if (session.tickCountdown()) {
				syncSessionState(session);
			}
		}
	}

	private void clearRequestsFor(UUID playerId) {
		String player = playerId.toString();
		pendingRequestTimes.entrySet().removeIf(entry -> entry.getKey().startsWith(player + ":") || entry.getKey().endsWith(":" + player));
		duplicateHintTimes.entrySet().removeIf(entry -> entry.getKey().startsWith(player + ":") || entry.getKey().endsWith(":" + player));
		requesterCooldowns.remove(playerId);
	}

	private String pairKey(UUID requesterId, UUID targetId) {
		return requesterId.toString() + ":" + targetId.toString();
	}
}
