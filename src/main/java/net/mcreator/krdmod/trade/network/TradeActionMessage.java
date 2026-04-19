package net.mcreator.krdmod.trade.network;

import net.mcreator.krdmod.trade.TradeManager;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TradeActionMessage implements IMessage {
	public static final int ACTION_TOGGLE_READY = 0;
	public static final int ACTION_CANCEL = 1;

	private int action;

	public TradeActionMessage() {
	}

	public TradeActionMessage(int action) {
		this.action = action;
	}

	@Override
	public void fromBytes(io.netty.buffer.ByteBuf buf) {
		action = buf.readInt();
	}

	@Override
	public void toBytes(io.netty.buffer.ByteBuf buf) {
		buf.writeInt(action);
	}

	public static class Handler implements IMessageHandler<TradeActionMessage, IMessage> {
		@Override
		public IMessage onMessage(TradeActionMessage message, MessageContext ctx) {
			ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> TradeManager.INSTANCE.handleAction(ctx.getServerHandler().player, message.action));
			return null;
		}
	}
}
