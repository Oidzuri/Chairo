package net.mcreator.krdmod.trade;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeSession {
	private static final int COUNTDOWN_TICKS = 60;
	private final UUID firstPlayerId;
	private final UUID secondPlayerId;
	private final TradeInventory firstOffer;
	private final TradeInventory secondOffer;
	private boolean firstReady;
	private boolean secondReady;
	private int countdownTicks = -1;
	private boolean completed;
	private boolean closed;
	private boolean suppressChanges;

	public TradeSession(UUID firstPlayerId, UUID secondPlayerId) {
		this.firstPlayerId = firstPlayerId;
		this.secondPlayerId = secondPlayerId;
		this.firstOffer = new TradeInventory("trade_first", firstPlayerId, this::onOfferChanged);
		this.secondOffer = new TradeInventory("trade_second", secondPlayerId, this::onOfferChanged);
	}

	public UUID getOtherPlayerId(UUID playerId) {
		return firstPlayerId.equals(playerId) ? secondPlayerId : firstPlayerId;
	}

	public TradeInventory getOwnOffer(UUID playerId) {
		return firstPlayerId.equals(playerId) ? firstOffer : secondOffer;
	}

	public TradeInventory getOtherOffer(UUID playerId) {
		return firstPlayerId.equals(playerId) ? secondOffer : firstOffer;
	}

	public boolean isOwnReady(UUID playerId) {
		return firstPlayerId.equals(playerId) ? firstReady : secondReady;
	}

	public boolean isOtherReady(UUID playerId) {
		return firstPlayerId.equals(playerId) ? secondReady : firstReady;
	}

	public boolean isActive() {
		return !completed && !closed;
	}

	public int getCountdownTicks() {
		return countdownTicks;
	}

	public void onOfferChanged(UUID playerId) {
		if (suppressChanges || !isActive()) {
			return;
		}
		cancelCountdown();
		boolean changed = setReadyInternal(playerId, false);
		changed = setReadyInternal(getOtherPlayerId(playerId), false) || changed;
		if (changed) {
			syncStates();
		}
	}

	public void toggleReady(UUID playerId) {
		if (!isActive()) {
			return;
		}
		boolean nextReady = !isOwnReady(playerId);
		setReadyInternal(playerId, nextReady);
		if (!nextReady) {
			cancelCountdown();
		} else if (firstReady && secondReady) {
			startCountdown();
		}
		syncStates();
	}

	public void cancel(EntityPlayerMP actor) {
		if (!isActive()) {
			return;
		}
		EntityPlayerMP other = TradeManager.INSTANCE.getPlayer(getOtherPlayerId(actor.getUniqueID()));
		sendMessage(actor, "Обмен отменен.");
		if (other != null) {
			sendMessage(other, "Обмен отменен игроком " + actor.getName() + ".");
		}
		closeAndReturn(true);
	}

	public void onContainerClosed(EntityPlayerMP actor) {
		if (completed || closed) {
			return;
		}
		EntityPlayerMP other = TradeManager.INSTANCE.getPlayer(getOtherPlayerId(actor.getUniqueID()));
		sendMessage(actor, "Обмен закрыт.");
		if (other != null) {
			sendMessage(other, actor.getName() + " закрыл окно трейда.");
		}
		closeAndReturn(true);
	}

	private void tryComplete() {
		if (!firstReady || !secondReady || !isActive()) {
			return;
		}
		EntityPlayerMP first = TradeManager.INSTANCE.getPlayer(firstPlayerId);
		EntityPlayerMP second = TradeManager.INSTANCE.getPlayer(secondPlayerId);
		if (first == null || second == null) {
			closeAndReturn(true);
			return;
		}
		List<ItemStack> firstItems = snapshot(firstOffer);
		List<ItemStack> secondItems = snapshot(secondOffer);
		if (!canFitAll(first, secondItems) || !canFitAll(second, firstItems)) {
			firstReady = false;
			secondReady = false;
			sendMessage(first, "Недостаточно места в инвентаре для завершения обмена.");
			sendMessage(second, "Недостаточно места в инвентаре для завершения обмена.");
			syncStates();
			return;
		}
		completed = true;
		suppressChanges = true;
		try {
			giveAll(first, secondItems);
			giveAll(second, firstItems);
			clearInventory(firstOffer);
			clearInventory(secondOffer);
		} finally {
			suppressChanges = false;
		}
		sendMessage(first, "Обмен успешно завершен.");
		sendMessage(second, "Обмен успешно завершен.");
		closeContainers(first, second);
		TradeManager.INSTANCE.finishSession(this);
	}

	public boolean tickCountdown() {
		if (!isActive() || countdownTicks < 0) {
			return false;
		}
		countdownTicks--;
		if (countdownTicks <= 0) {
			countdownTicks = -1;
			tryComplete();
			return false;
		}
		return true;
	}

	private void closeAndReturn(boolean shouldCloseContainers) {
		if (closed || completed) {
			TradeManager.INSTANCE.finishSession(this);
			return;
		}
		closed = true;
		suppressChanges = true;
		try {
			EntityPlayerMP first = TradeManager.INSTANCE.getPlayer(firstPlayerId);
			EntityPlayerMP second = TradeManager.INSTANCE.getPlayer(secondPlayerId);
			returnAll(first, firstOffer);
			returnAll(second, secondOffer);
			clearInventory(firstOffer);
			clearInventory(secondOffer);
			if (shouldCloseContainers) {
				closeContainers(first, second);
			}
		} finally {
			suppressChanges = false;
			TradeManager.INSTANCE.finishSession(this);
		}
	}

	private void closeContainers(EntityPlayerMP first, EntityPlayerMP second) {
		if (first != null && first.openContainer instanceof TradeContainer) {
			first.closeScreen();
		}
		if (second != null && second.openContainer instanceof TradeContainer) {
			second.closeScreen();
		}
	}

	private void syncStates() {
		TradeManager.INSTANCE.syncSessionState(this);
	}

	private void startCountdown() {
		countdownTicks = COUNTDOWN_TICKS;
	}

	private void cancelCountdown() {
		countdownTicks = -1;
	}

	private boolean setReadyInternal(UUID playerId, boolean ready) {
		if (firstPlayerId.equals(playerId)) {
			boolean changed = firstReady != ready;
			firstReady = ready;
			return changed;
		}
		boolean changed = secondReady != ready;
		secondReady = ready;
		return changed;
	}

	private static List<ItemStack> snapshot(TradeInventory inventory) {
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				items.add(stack.copy());
			}
		}
		return items;
	}

	private static void returnAll(EntityPlayerMP player, TradeInventory inventory) {
		if (player == null) {
			return;
		}
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			ItemStack copy = stack.copy();
			if (!player.inventory.addItemStackToInventory(copy) && !copy.isEmpty()) {
				player.dropItem(copy, false);
			}
		}
		player.inventoryContainer.detectAndSendChanges();
	}

	private static void clearInventory(TradeInventory inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	private static void giveAll(EntityPlayerMP player, List<ItemStack> items) {
		for (ItemStack stack : items) {
			ItemStack copy = stack.copy();
			if (!player.inventory.addItemStackToInventory(copy) && !copy.isEmpty()) {
				player.dropItem(copy, false);
			}
		}
		player.inventoryContainer.detectAndSendChanges();
	}

	private static boolean canFitAll(EntityPlayerMP player, List<ItemStack> items) {
		if (player == null || player.isCreative()) {
			return true;
		}
		InventoryPlayer inventory = player.inventory;
		NonNullList<ItemStack> simulated = NonNullList.withSize(inventory.mainInventory.size(), ItemStack.EMPTY);
		for (int i = 0; i < inventory.mainInventory.size(); i++) {
			ItemStack stack = inventory.mainInventory.get(i);
			simulated.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
		}
		for (ItemStack original : items) {
			ItemStack incoming = original.copy();
			insertSimulated(simulated, incoming, inventory.getInventoryStackLimit());
			if (!incoming.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static void insertSimulated(NonNullList<ItemStack> simulated, ItemStack incoming, int inventoryLimit) {
		if (incoming.isStackable()) {
			for (int i = 0; i < simulated.size() && !incoming.isEmpty(); i++) {
				ItemStack existing = simulated.get(i);
				if (existing.isEmpty() || !ItemStack.areItemsEqual(existing, incoming)
						|| !ItemStack.areItemStackTagsEqual(existing, incoming)) {
					continue;
				}
				int max = Math.min(existing.getMaxStackSize(), inventoryLimit);
				int space = max - existing.getCount();
				if (space <= 0) {
					continue;
				}
				int move = Math.min(space, incoming.getCount());
				existing.grow(move);
				incoming.shrink(move);
			}
		}
		for (int i = 0; i < simulated.size() && !incoming.isEmpty(); i++) {
			if (!simulated.get(i).isEmpty()) {
				continue;
			}
			simulated.set(i, incoming.copy());
			incoming.setCount(0);
		}
	}

	private static void sendMessage(EntityPlayerMP player, String message) {
		if (player != null) {
			player.sendMessage(new TextComponentString(message));
		}
	}

}
