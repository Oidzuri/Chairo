package net.mcreator.krdmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

@ElementsKrdModMod.ModElement.Tag
public class KRDLogic extends ElementsKrdModMod.ModElement {
	private final ResourceLocation golodTexture = new ResourceLocation("krd_mod:textures/gui/golod.png");
	private final ResourceLocation opitTexture = new ResourceLocation("krd_mod:textures/gui/opit.png");
	private final ResourceLocation hpTexture = new ResourceLocation("krd_mod:textures/gui/xp.png"); // Новая текстура для ХП
	private final ResourceLocation logoTexture = new ResourceLocation("krd_mod:textures/gui/logo.png");
	private final ResourceLocation barEmptyTexture = new ResourceLocation("krd_mod:textures/gui/xpbarpustxp.png");
	private final ResourceLocation heartIcon = new ResourceLocation("krd_mod:textures/gui/heart.png");
	private final ResourceLocation foodIcon = new ResourceLocation("krd_mod:textures/gui/food.png");
	private final ResourceLocation expIcon = new ResourceLocation("krd_mod:textures/gui/exp_icon.png");

	private long lastDamageTime = 0, lastHealTime = 0, lastFoodGainTime = 0;
	private float lastHealth = 20.0F;
	private int lastFood = 20;

	public KRDLogic(ElementsKrdModMod instance) {
		super(instance, 1);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		if (event.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(new ClientHudRenderer());
		}
	}

	@SideOnly(Side.CLIENT)
	private class ClientHudRenderer {
		@SubscribeEvent
		public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) drawCustomHUD();
			if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD
					|| event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE
					|| event.getType() == RenderGameOverlayEvent.ElementType.AIR) event.setCanceled(true);
		}

		private void drawCustomHUD() {
			net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
			if (mc.player == null) return;

			float currentHp = mc.player.getHealth();
			int currentFood = mc.player.getFoodStats().getFoodLevel();
			if (currentHp < lastHealth) lastDamageTime = System.currentTimeMillis();
			else if (currentHp > lastHealth) lastHealTime = System.currentTimeMillis();
			if (currentFood > lastFood) lastFoodGainTime = System.currentTimeMillis();
			lastHealth = currentHp; lastFood = currentFood;

			float shakeX = 0, shakeY = 0;
			long dt = System.currentTimeMillis() - lastDamageTime;
			if (dt < 400) {
				shakeX = (float) (Math.sin(dt * 0.5) * 2 * (1.0 - dt / 400.0));
				shakeY = (float) (Math.cos(dt * 0.5) * 2 * (1.0 - dt / 400.0));
			}

			int mX = (int) (12 + shakeX);
			int mY = (int) (12 + shakeY);
			int lSize = 38;
			int bW = 90;  // Уменьшенная ширина
			int bH = 9;   // Уменьшенная высота
			int bX = mX + lSize + 20; 
			int sY = mY + 20;

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
					GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);

			int bgColor = 0xCC001A1A; 
			float breath = (float) Math.sin(System.currentTimeMillis() / 600.0) * 0.2F + 0.8F;
			int bCol = ((int)(255 * breath) << 24) | 0x008B8B; 
			
			if (currentHp / mc.player.getMaxHealth() < 0.3F) {
				bCol = (0xFF << 24) | ((int)(150 + 105 * Math.sin(System.currentTimeMillis()/100.0)) << 16);
			}
			
			// Динамический фон под размер полосок
			Gui.drawRect(mX - 6, mY - 6, bX + bW + 8, sY + (bH + 5) * 3 + 2, bgColor);
			drawFrame(mX - 6, mY - 6, bX + bW + 8, sY + (bH + 5) * 3 + 2, bCol);
			Gui.drawRect(mX + lSize + 6, mY - 2, mX + lSize + 7, sY + (bH + 5) * 3 - 2, 0x4400FFFF);

			// Лого
			GlStateManager.color(1, 1, 1, 1);
			mc.getTextureManager().bindTexture(logoTexture);
			Gui.drawModalRectWithCustomSizedTexture(mX, mY, 0, 0, lSize, lSize, lSize, lSize);

			// Имя
			mc.fontRenderer.drawStringWithShadow(mc.player.getName(), bX, mY - 4, 0xFFFFFF);

			// Уровень и Ранг
			GlStateManager.pushMatrix();
			float infoScale = 0.8F; 
			GlStateManager.scale(infoScale, infoScale, 1.0F);
			int infoY = (int)((mY + 9) / infoScale);
			int levelX = (int)(bX / infoScale);
			int level = ServerLevelBridge.getLevel(mc);
			int progress = ServerLevelBridge.getProgressPercent(mc);
			String rank = ServerLevelBridge.getRank(mc);
			String lvlTxt = "Уровень: " + level;
			mc.fontRenderer.drawStringWithShadow(lvlTxt, levelX, infoY, 0x00FDFF);
			int lvlWidth = mc.fontRenderer.getStringWidth(lvlTxt);
			int rankX = levelX + lvlWidth + 12; 
			mc.fontRenderer.drawStringWithShadow("Ранг: " + rank, rankX, infoY, 0xFFD700);
			GlStateManager.popMatrix();

			// Бары (HP теперь использует xp.png)
			drawIcon(mc, bX - 14, sY + 1, 8, heartIcon);
			renderBar(mc, bX, sY, bW, bH, currentHp/mc.player.getMaxHealth(), hpTexture, 0, (int)currentHp + "/" + (int)mc.player.getMaxHealth(), lastHealTime);

			drawIcon(mc, bX - 14, sY + bH + 6, 8, foodIcon);
			renderBar(mc, bX, sY + bH + 5, bW, bH, currentFood/20.0F, golodTexture, 0, currentFood + "/20", lastFoodGainTime);

			drawIcon(mc, bX - 14, sY + (bH + 5) * 2 + 1, 8, expIcon);
			renderBar(mc, bX, sY + (bH + 5) * 2, bW, bH, progress / 100.0F, opitTexture, 0, progress + "%", 0);

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}

		private void renderBar(net.minecraft.client.Minecraft mc, int x, int y, int w, int h, float pct, ResourceLocation tex, int col, String txt, long uTime) {
			mc.getTextureManager().bindTexture(barEmptyTexture);
			Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
			int in = 1; // Уменьшил внутренний отступ для маленьких полосок
			int fw = (int) ((w - (in * 2)) * pct);
			if (fw > 0) {
				if (tex != null) {
					mc.getTextureManager().bindTexture(tex);
					Gui.drawModalRectWithCustomSizedTexture(x + in, y + in, 0, 0, fw, h - (in * 2), w - (in * 2), h - (in * 2));
				} else {
					Gui.drawRect(x + in, y + in, x + in + fw, y + h - in, col);
				}
				
				float fl = Math.max(0, 1.0F - (System.currentTimeMillis() - uTime) / 500.0F);
				if (fl > 0) Gui.drawRect(x + in, y + in, x + in + fw, y + h - in, ((int)(fl * 160) << 24) | 0xFFFFFF);
			}
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.65F, 0.65F, 1.0F); // Текст в барах тоже чуть меньше
			int tw = mc.fontRenderer.getStringWidth(txt);
			mc.fontRenderer.drawStringWithShadow(txt, (x + w/2f)/0.65F - tw/2f, (y + h/2f)/0.65F - 3, 0xFFFFFF);
			GlStateManager.popMatrix();
		}

		private void drawIcon(net.minecraft.client.Minecraft mc, int x, int y, int s, ResourceLocation r) {
			mc.getTextureManager().bindTexture(r);
			GlStateManager.color(1, 1, 1, 1);
			Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, s, s, s, s);
		}

		private void drawFrame(int l, int t, int r, int b, int c) {
			Gui.drawRect(l, t, r, t + 1, c); Gui.drawRect(l, b - 1, r, b, c);
			Gui.drawRect(l, t, l + 1, b, c); Gui.drawRect(r - 1, t, r, b, c);
		}
	}
	
	@Override public void initElements() {}
	@Override public void preInit(net.minecraftforge.fml.common.event.FMLPreInitializationEvent e) {}
	@Override public void generateWorld(java.util.Random r, int x, int z, net.minecraft.world.World w, int d, net.minecraft.world.gen.IChunkGenerator cg, net.minecraft.world.chunk.IChunkProvider cp) {}
	@Override public void serverLoad(net.minecraftforge.fml.common.event.FMLServerStartingEvent e) {}
	@Override public void registerModels(net.minecraftforge.client.event.ModelRegistryEvent e) {}
}
