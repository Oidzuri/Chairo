package net.mcreator.krdmod.trade;

import net.mcreator.krdmod.KrdModMod;
import net.mcreator.krdmod.trade.network.TradeRequestMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public final class TradeKeyHandler {
	private static final KeyBinding OPEN_TRADE_KEY =
			new KeyBinding("Открыть трейд", Keyboard.KEY_R, "KRD Mod");

	private TradeKeyHandler() {
	}

	public static void registerKeybinding() {
		ClientRegistry.registerKeyBinding(OPEN_TRADE_KEY);
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
		while (OPEN_TRADE_KEY.isPressed()) {
			GuiScreen current = mc.currentScreen;
			if (current != null) {
				continue;
			}
			EntityPlayer target = getLookedAtPlayer(mc);
			if (target == null || target == mc.player) {
				mc.player.sendMessage(new net.minecraft.util.text.TextComponentString("Наведитесь на игрока, чтобы открыть трейд."));
				continue;
			}
			KrdModMod.PACKET_HANDLER.sendToServer(new TradeRequestMessage(target.getName()));
		}
	}

	private static EntityPlayer getLookedAtPlayer(Minecraft mc) {
		RayTraceResult hit = mc.objectMouseOver;
		if (hit == null || hit.typeOfHit != RayTraceResult.Type.ENTITY) {
			return null;
		}
		Entity entity = hit.entityHit;
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
	}
}
