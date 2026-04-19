package net.mcreator.krdmod.trade.network;

import net.mcreator.krdmod.KrdModMod;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenTradeGuiMessage implements IMessage {
	private String otherPlayerName;
	private int windowId;

	public OpenTradeGuiMessage() {
	}

	public OpenTradeGuiMessage(String otherPlayerName, int windowId) {
		this.otherPlayerName = otherPlayerName;
		this.windowId = windowId;
	}

	@Override
	public void fromBytes(io.netty.buffer.ByteBuf buf) {
		otherPlayerName = ByteBufUtils.readUTF8String(buf);
		windowId = buf.readInt();
	}

	@Override
	public void toBytes(io.netty.buffer.ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, otherPlayerName);
		buf.writeInt(windowId);
	}

	public static class Handler implements IMessageHandler<OpenTradeGuiMessage, IMessage> {
		@Override
		public IMessage onMessage(OpenTradeGuiMessage message, MessageContext ctx) {
			KrdModMod.proxy.openTradeGui(message.otherPlayerName, message.windowId);
			return null;
		}
	}
}
