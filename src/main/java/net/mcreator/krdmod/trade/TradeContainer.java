package net.mcreator.krdmod.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TradeContainer extends Container {
	private static final int GUI_WIDTH = 278;
	private static final int GUI_HEIGHT = 246;
	private static final int PLAYER_INV_START = 0;
	private static final int PLAYER_INV_END = 36;
	private static final int OWN_TRADE_START = 36;
	private static final int OWN_TRADE_END = 45;

	private final TradeSession session;
	private final String otherPlayerName;
	private boolean ownReady;
	private boolean otherReady;
	private int countdownTicks = -1;

	public TradeContainer(EntityPlayer player, TradeSession session, String otherPlayerName) {
		this(player, session, otherPlayerName, session.getOwnOffer(player.getUniqueID()), session.getOtherOffer(player.getUniqueID()));
	}

	public TradeContainer(EntityPlayer player, String otherPlayerName, int windowId) {
		this(player, null, otherPlayerName, new TradeInventory("trade_local", null, null), new InventoryBasic("trade_remote", false, 9));
		this.windowId = windowId;
	}

	private TradeContainer(EntityPlayer player, TradeSession session, String otherPlayerName, IInventory ownOffer, IInventory otherOffer) {
		this.session = session;
		this.otherPlayerName = otherPlayerName;
		addPlayerSlots(player.inventory);
		addTradeSlots(ownOffer, otherOffer);
	}

	private void addPlayerSlots(IInventory inventoryPlayer) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9, 58 + col * 18, 168 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			addSlotToContainer(new Slot(inventoryPlayer, col, 58 + col * 18, 226));
		}
	}

	private void addTradeSlots(IInventory ownOffer, IInventory otherOffer) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int slotIndex = col + row * 3;
				addSlotToContainer(new Slot(ownOffer, slotIndex, 37 + col * 18, 44 + row * 18));
				addSlotToContainer(new Slot(otherOffer, slotIndex, 187 + col * 18, 44 + row * 18) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						return false;
					}
				});
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return session == null || session.isActive();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getStack();
		result = stack.copy();
		if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
			if (!mergeItemStack(stack, OWN_TRADE_START, OWN_TRADE_END, false)) {
				return ItemStack.EMPTY;
			}
		} else if (index >= OWN_TRADE_START && index < OWN_TRADE_END) {
			if (!mergeItemStack(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		if (stack.getCount() == result.getCount()) {
			return ItemStack.EMPTY;
		}
		slot.onTake(playerIn, stack);
		return result;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (!playerIn.world.isRemote && session != null && playerIn instanceof EntityPlayerMP) {
			session.onContainerClosed((EntityPlayerMP) playerIn);
		}
	}

	public void setReadyStates(boolean ownReady, boolean otherReady, int countdownTicks) {
		this.ownReady = ownReady;
		this.otherReady = otherReady;
		this.countdownTicks = countdownTicks;
	}

	public boolean isOwnReady() {
		return ownReady;
	}

	public boolean isOtherReady() {
		return otherReady;
	}

	public int getCountdownTicks() {
		return countdownTicks;
	}

	public String getOtherPlayerName() {
		return otherPlayerName;
	}

	public int getGuiWidth() {
		return GUI_WIDTH;
	}

	public int getGuiHeight() {
		return GUI_HEIGHT;
	}
}
