package net.mcreator.krdmod.trade.network;

import net.mcreator.krdmod.KrdModMod;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TradeStateMessage implements IMessage {
	private int windowId;
	private boolean ownReady;
	private boolean otherReady;
	private int countdownTicks;

	public TradeStateMessage() {
	}

	public TradeStateMessage(int windowId, boolean ownReady, boolean otherReady, int countdownTicks) {
		this.windowId = windowId;
		this.ownReady = ownReady;
		this.otherReady = otherReady;
		this.countdownTicks = countdownTicks;
	}

	@Override
	public void fromBytes(io.netty.buffer.ByteBuf buf) {
		windowId = buf.readInt();
		ownReady = buf.readBoolean();
		otherReady = buf.readBoolean();
		countdownTicks = buf.readInt();
	}

	@Override
	public void toBytes(io.netty.buffer.ByteBuf buf) {
		buf.writeInt(windowId);
		buf.writeBoolean(ownReady);
		buf.writeBoolean(otherReady);
		buf.writeInt(countdownTicks);
	}

	public static class Handler implements IMessageHandler<TradeStateMessage, IMessage> {
		@Override
		public IMessage onMessage(TradeStateMessage message, MessageContext ctx) {
			KrdModMod.proxy.updateTradeState(message.windowId, message.ownReady, message.otherReady, message.countdownTicks);
			return null;
		}
	}
}
