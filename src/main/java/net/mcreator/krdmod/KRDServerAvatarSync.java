package net.mcreator.krdmod;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

public final class KRDServerAvatarSync {
    private static final String SERVER_AVATAR_DIR = "C:\\Users\\Administrator\\ChairoLand\\Server-main\\krd_avatars";
    private static final long MAX_AVATAR_BYTES = 1024L * 1024L;

    private KRDServerAvatarSync() {
    }

    public static void sendAllToPlayer(EntityPlayerMP player) {
        if (player == null) {
            return;
        }

        File dir = new File(SERVER_AVATAR_DIR);
        if (!dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!isSupportedAvatar(file)) {
                continue;
            }
            if (file.length() <= 0L || file.length() > MAX_AVATAR_BYTES) {
                continue;
            }
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String name = trimExtension(file.getName());
                KrdModMod.PACKET_HANDLER.sendTo(new AvatarSyncMessage(name, bytes), player);
            } catch (IOException ignored) {
            }
        }
    }

    private static boolean isSupportedAvatar(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String name = file.getName().toLowerCase(Locale.ROOT);
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    private static String trimExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    public static class AvatarSyncMessage implements IMessage {
        private String playerName;
        private byte[] data;

        public AvatarSyncMessage() {
        }

        public AvatarSyncMessage(String playerName, byte[] data) {
            this.playerName = playerName == null ? "" : playerName;
            this.data = data == null ? new byte[0] : data;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeUTF8String(buf, playerName);
            buf.writeInt(data.length);
            buf.writeBytes(data);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            playerName = ByteBufUtils.readUTF8String(buf);
            int length = Math.max(0, buf.readInt());
            data = new byte[length];
            buf.readBytes(data);
        }
    }

    public static class AvatarSyncMessageHandler implements IMessageHandler<AvatarSyncMessage, IMessage> {
        @Override
        public IMessage onMessage(AvatarSyncMessage message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                        KRDTabOverlay.receiveSyncedAvatar(message.playerName, message.data));
            }
            return null;
        }
    }
}
