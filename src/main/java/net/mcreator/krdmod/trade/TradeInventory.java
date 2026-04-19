package net.mcreator.krdmod.trade;

import net.minecraft.inventory.InventoryBasic;

import java.util.UUID;
import java.util.function.Consumer;

public class TradeInventory extends InventoryBasic {
	private final UUID ownerId;
	private final Consumer<UUID> changeCallback;

	public TradeInventory(String name, UUID ownerId, Consumer<UUID> changeCallback) {
		super(name, false, 9);
		this.ownerId = ownerId;
		this.changeCallback = changeCallback;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (changeCallback != null && ownerId != null) {
			changeCallback.accept(ownerId);
		}
	}
}
