package net.mcreator.krdmod.trade.network;

import net.mcreator.krdmod.trade.TradeManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;

public class TradeRequestMessage implements IMessage {
	private String targetName;

	public TradeRequestMessage() {
	}

	public TradeRequestMessage(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public void fromBytes(io.netty.buffer.ByteBuf buf) {
		targetName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(io.netty.buffer.ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, targetName);
	}

	public static class Handler implements IMessageHandler<TradeRequestMessage, IMessage> {
		@Override
		public IMessage onMessage(TradeRequestMessage message, MessageContext ctx) {
			ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
				EntityPlayerMP requester = ctx.getServerHandler().player;
				EntityPlayerMP target = requester.getServerWorld().getMinecraftServer().getPlayerList().getPlayerByUsername(message.targetName);
				if (target == null) {
					requester.sendMessage(new net.minecraft.util.text.TextComponentString("Игрок не найден."));
					return;
				}
				TradeManager.INSTANCE.requestTrade(requester, target);
			});
			return null;
		}
	}
}
