/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class ElementsKrdModMod.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/
package net.mcreator.krdmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.World;

import java.util.Random;

@ElementsKrdModMod.ModElement.Tag
public class KRDLogic extends ElementsKrdModMod.ModElement {
	private final ResourceLocation xpTexture = new ResourceLocation("krd_mod:textures/gui/xp.png");
	private final ResourceLocation golodTexture = new ResourceLocation("krd_mod:textures/gui/golod.png");
	private final ResourceLocation opitTexture = new ResourceLocation("krd_mod:textures/gui/opit.png");
	private final ResourceLocation xpbar1Texture = new ResourceLocation("krd_mod:textures/gui/xpbar1.png");
	private final ResourceLocation xpbarpustxpTexture = new ResourceLocation("krd_mod:textures/gui/xpbarpustxp.png");
	private float currentSpecialExp = 50.0F;
	private float maxSpecialExp = 100.0F;
	private int tempLevel = 1;

	/**
	 * Do not remove this constructor
	 */
	public KRDLogic(ElementsKrdModMod instance) {
		super(instance, 1);
	}

	@Override
	public void initElements() {
	}

	@Override
	public void init(FMLInitializationEvent event) {
		if (event.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(new ClientHudRenderer());
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
	}

	@Override
	public void generateWorld(Random random, int posX, int posZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {
	}

	@Override
	public void registerModels(ModelRegistryEvent event) {
	}

	@SideOnly(Side.CLIENT)
	private class ClientHudRenderer {
		@SubscribeEvent
		public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
				drawCustomHUD();
			}

			if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH
					|| event.getType() == RenderGameOverlayEvent.ElementType.FOOD
					|| event.getType() == RenderGameOverlayEvent.ElementType.ARMOR
					|| event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE
					|| event.getType() == RenderGameOverlayEvent.ElementType.AIR) {
				event.setCanceled(true);
			}
		}

		private void drawCustomHUD() {
			net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getMinecraft();
			if (minecraft.player == null || minecraft.world == null) {
				return;
			}

			int marginX = 6;
			int marginY = 6;
			int textureWidth = 92;
			int textureHeight = 10;
			int logoSize = 32;
			int x = marginX + logoSize + 8;
			int nameTextX = x;
			int nameTextY = marginY + 1;
			int rankTextX = x;
			int rankTextY = marginY + 11;
			int healthY = marginY + 22;
			int foodY = healthY + textureHeight + 3;
			int expY = foodY + textureHeight + 3;
			int inset = 2;
			int innerWidth = textureWidth - inset * 2;
			int innerHeight = textureHeight - inset * 2;
			int blockLeft = marginX - 3;
			int blockTop = marginY - 3;
			int blockRight = x + textureWidth + 4;
			int blockBottom = expY + textureHeight + 4;

			float currentHealth = minecraft.player.getHealth();
			float maxHealth = minecraft.player.getMaxHealth();
			float healthRatio = maxHealth > 0.0F ? currentHealth / maxHealth : 0.0F;
			healthRatio = Math.max(0.0F, Math.min(1.0F, healthRatio));

			int currentFood = minecraft.player.getFoodStats().getFoodLevel();
			int maxFood = 20;
			float foodRatio = maxFood > 0 ? (float) currentFood / (float) maxFood : 0.0F;
			foodRatio = Math.max(0.0F, Math.min(1.0F, foodRatio));

			float expRatio = maxSpecialExp > 0.0F ? currentSpecialExp / maxSpecialExp : 0.0F;
			expRatio = Math.max(0.0F, Math.min(1.0F, expRatio));

			int healthWidth = Math.round(healthRatio * innerWidth);
			int foodWidth = Math.round(foodRatio * innerWidth);
			int expWidth = Math.round(expRatio * innerWidth);

			float barTextScale = 0.75F;

			net.minecraft.client.renderer.GlStateManager.pushMatrix();
			net.minecraft.client.renderer.GlStateManager.enableBlend();
			net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			// Dark panel to visually group the HUD.
			net.minecraft.client.gui.Gui.drawRect(blockLeft, blockTop, blockRight, blockBottom, 0x5A000000);

			// Fallback vector logo to avoid black-square texture issues.
			net.minecraft.client.gui.Gui.drawRect(marginX, marginY, marginX + logoSize, marginY + logoSize, 0xFF1A2342);
			net.minecraft.client.gui.Gui.drawRect(marginX + 2, marginY + 2, marginX + logoSize - 2, marginY + logoSize - 2, 0xFF0B0F1E);
			minecraft.fontRenderer.drawStringWithShadow("KRD", marginX + 7, marginY + 12, 0xFF9FB6FF);

			String playerName = minecraft.player.getName();
			minecraft.fontRenderer.drawString(playerName, nameTextX, nameTextY, 0xFFFFFF);

			// Here will be rank loaded from MySQL
			String rankPlaceholder = "\u0420\u0430\u043D\u0433: ...";
			String levelText = "\u0423\u0440\u043E\u0432\u0435\u043D\u044C:" + tempLevel;
			net.minecraft.client.renderer.GlStateManager.pushMatrix();
			net.minecraft.client.renderer.GlStateManager.scale(0.8F, 0.8F, 1.0F);
			int scaledRankX = (int) (rankTextX / 0.8F);
			int scaledRankY = (int) (rankTextY / 0.8F);
			minecraft.fontRenderer.drawString(rankPlaceholder, scaledRankX, scaledRankY, 0xB0B0B0);
			int scaledLevelX = scaledRankX + minecraft.fontRenderer.getStringWidth(rankPlaceholder) + 8;
			minecraft.fontRenderer.drawString(levelText, scaledLevelX, scaledRankY, 0xB0B0B0);
			net.minecraft.client.renderer.GlStateManager.popMatrix();

			// HP background
			drawTexture(minecraft, xpbarpustxpTexture, x, healthY, textureWidth, textureHeight);

			// HP fill as plain color (no xp texture)
			int healthR;
			int healthG;
			int healthB;
			// Red -> Yellow -> Green, clean anchors (avoid dirty green)
			if (healthRatio < 0.5F) {
				float k = healthRatio / 0.5F;
				healthR = 255;
				healthG = Math.round(90.0F + (220.0F - 90.0F) * k);
				healthB = Math.round(90.0F + (95.0F - 90.0F) * k);
			} else {
				float k = (healthRatio - 0.5F) / 0.5F;
				healthR = Math.round(255.0F - (255.0F - 95.0F) * k);
				healthG = Math.round(220.0F + (255.0F - 220.0F) * k);
				healthB = Math.round(95.0F + (120.0F - 95.0F) * k);
			}
			int healthFillColor = (0xCC << 24) | (healthR << 16) | (healthG << 8) | healthB;
			if (healthWidth > 0) {
				net.minecraft.client.gui.Gui.drawRect(x + inset, healthY + inset, x + inset + healthWidth, healthY + inset + innerHeight, healthFillColor);
				// Tiny gradient at the top of the fill (instead of harsh glint)
				int topR = Math.min(255, healthR + 40);
				int topG = Math.min(255, healthG + 40);
				int topB = Math.min(255, healthB + 40);
				int grad1 = (0x55 << 24) | (topR << 16) | (topG << 8) | topB;
				int grad2 = (0x22 << 24) | (topR << 16) | (topG << 8) | topB;
				net.minecraft.client.gui.Gui.drawRect(x + inset, healthY + inset, x + inset + healthWidth, healthY + inset + 1, grad1);
				net.minecraft.client.gui.Gui.drawRect(x + inset, healthY + inset + 1, x + inset + healthWidth, healthY + inset + 2, grad2);
			}

			String healthText = (int) currentHealth + " / " + (int) maxHealth;
			int healthTextLeft = x + (textureWidth / 2) - (int) (minecraft.fontRenderer.getStringWidth(healthText) / 2);
			int healthTextTop = healthY + (textureHeight / 2) - minecraft.fontRenderer.FONT_HEIGHT / 2;
			net.minecraft.client.renderer.GlStateManager.pushMatrix();
			net.minecraft.client.renderer.GlStateManager.scale(barTextScale, barTextScale, 1.0F);
			minecraft.fontRenderer.drawStringWithShadow(healthText, (int) (healthTextLeft / barTextScale), (int) (healthTextTop / barTextScale), 0xFFFFFF);
			net.minecraft.client.renderer.GlStateManager.popMatrix();

			// Food background + fill
			drawTexture(minecraft, xpbarpustxpTexture, x, foodY, textureWidth, textureHeight);
			if (foodWidth > 0) {
				drawTexturePart(minecraft, golodTexture, x + inset, foodY + inset, foodWidth, innerHeight, textureWidth, textureHeight);
				// subtle gradient at the top of the fill
				int gradA = 0x2AFFFFFF;
				net.minecraft.client.gui.Gui.drawRect(x + inset, foodY + inset, x + inset + foodWidth, foodY + inset + 1, gradA);
				net.minecraft.client.gui.Gui.drawRect(x + inset, foodY + inset + 1, x + inset + foodWidth, foodY + inset + 2, 0x1AFFFFFF);
			}

			String foodText = currentFood + " / " + maxFood;
			int foodTextLeft = x + (textureWidth / 2) - minecraft.fontRenderer.getStringWidth(foodText) / 2;
			int foodTextTop = foodY + (textureHeight / 2) - minecraft.fontRenderer.FONT_HEIGHT / 2 + 1; // slightly down
			net.minecraft.client.renderer.GlStateManager.pushMatrix();
			net.minecraft.client.renderer.GlStateManager.scale(barTextScale, barTextScale, 1.0F);
			minecraft.fontRenderer.drawStringWithShadow(foodText, (int) (foodTextLeft / barTextScale), (int) (foodTextTop / barTextScale), 0xFFFFFF);
			net.minecraft.client.renderer.GlStateManager.popMatrix();

			// Experience background + fill
			drawTexture(minecraft, xpbarpustxpTexture, x, expY, textureWidth, textureHeight);
			if (expWidth > 0) {
				drawTexturePart(minecraft, opitTexture, x + inset, expY + inset, expWidth, innerHeight, textureWidth, textureHeight);
				// subtle gradient at the top of the fill
				net.minecraft.client.gui.Gui.drawRect(x + inset, expY + inset, x + inset + expWidth, expY + inset + 1, 0x2AFFFFFF);
				net.minecraft.client.gui.Gui.drawRect(x + inset, expY + inset + 1, x + inset + expWidth, expY + inset + 2, 0x1AFFFFFF);
			}

			String expText = (int) currentSpecialExp + " / " + (int) maxSpecialExp;
			int expTextLeft = x + (textureWidth / 2) - minecraft.fontRenderer.getStringWidth(expText) / 2;
			int expTextTop = expY + (textureHeight / 2) - minecraft.fontRenderer.FONT_HEIGHT / 2 + 1; // slightly down
			net.minecraft.client.renderer.GlStateManager.pushMatrix();
			net.minecraft.client.renderer.GlStateManager.scale(barTextScale, barTextScale, 1.0F);
			minecraft.fontRenderer.drawStringWithShadow(expText, (int) (expTextLeft / barTextScale), (int) (expTextTop / barTextScale), 0xFFFFFF);
			net.minecraft.client.renderer.GlStateManager.popMatrix();

			net.minecraft.client.renderer.GlStateManager.disableBlend();
			net.minecraft.client.renderer.GlStateManager.popMatrix();
		}

		private void drawTexture(net.minecraft.client.Minecraft minecraft, ResourceLocation texture, int x, int y, int width, int height) {
			minecraft.getTextureManager().bindTexture(texture);
			net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		}

		private void drawTexturePart(net.minecraft.client.Minecraft minecraft, ResourceLocation texture, int x, int y, int drawWidth, int drawHeight,
				int textureWidth, int textureHeight) {
			if (drawWidth <= 0 || drawHeight <= 0) {
				return;
			}
			minecraft.getTextureManager().bindTexture(texture);
			net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, drawWidth, drawHeight, textureWidth, textureHeight);
		}
	}
}
