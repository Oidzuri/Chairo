package net.mcreator.krdmod.trade;

import net.mcreator.krdmod.EscapeMenu;
import net.mcreator.krdmod.KrdModMod;
import net.mcreator.krdmod.trade.network.TradeActionMessage;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

public class TradeGui extends GuiContainer {
	private static final int PANEL_FILL = 0xD0121822;
	private static final int PANEL_GLOW = 0x2A63E6FF;
	private static final int PANEL_DEEP = 0xA00A0E15;
	private static final int CARD_FILL = 0x9819212C;
	private static final int CARD_EDGE = 0x2A6FE8FF;
	private static final int SLOT_GRID = 0x201C2732;
	private static final int TEXT_PRIMARY = 0xEDF8FF;
	private static final int TEXT_MUTED = 0x90AEC1;
	private static final int TEXT_ACCENT = 0x63E6FF;
	private static final int TEXT_READY = 0x6EF0AF;
	private static final int TEXT_WARN = 0xFFD166;

	private final TradeContainer container;
	private GuiButton readyButton;
	private GuiButton cancelButton;

	public TradeGui(TradeContainer container) {
		super(container);
		this.container = container;
		this.xSize = container.getGuiWidth();
		this.ySize = container.getGuiHeight();
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		readyButton = new EscapeMenu.UILeftButton(0, guiLeft + 38, guiTop + 111, 88, 24, "");
		cancelButton = new EscapeMenu.UILeftButton(1, guiLeft + 152, guiTop + 111, 88, 24, "Отмена");
		buttonList.add(readyButton);
		buttonList.add(cancelButton);
		updateButtonState();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			KrdModMod.PACKET_HANDLER.sendToServer(new TradeActionMessage(TradeActionMessage.ACTION_TOGGLE_READY));
		} else if (button.id == 1) {
			KrdModMod.PACKET_HANDLER.sendToServer(new TradeActionMessage(TradeActionMessage.ACTION_CANCEL));
			mc.player.closeScreen();
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		updateButtonState();
	}

	private void updateButtonState() {
		if (readyButton == null) {
			return;
		}
		boolean countdown = container.getCountdownTicks() > 0;
		readyButton.displayString = countdown ? "Подтверждено" : (container.isOwnReady() ? "Не готов" : "Готов");
		readyButton.enabled = !countdown;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString("Обмен с " + container.getOtherPlayerName(), 18, 14, TEXT_PRIMARY);
		fontRenderer.drawString("Ваше предложение", 28, 30, TEXT_ACCENT);
		fontRenderer.drawString("Предложение партнера", 174, 30, TEXT_ACCENT);
		fontRenderer.drawString("Инвентарь", 18, 153, TEXT_ACCENT);

		fontRenderer.drawString("Вы: " + statusLabel(container.isOwnReady()), 38, 95, container.isOwnReady() ? TEXT_READY : TEXT_MUTED);
		fontRenderer.drawString("Партнер: " + statusLabel(container.isOtherReady()), 154, 95, container.isOtherReady() ? TEXT_READY : TEXT_MUTED);

		if (container.getCountdownTicks() > 0) {
			int seconds = (container.getCountdownTicks() + 19) / 20;
			fontRenderer.drawString("Подтверждено. Обмен завершится через " + seconds + " сек", 38, 142, TEXT_WARN);
		} else {
			fontRenderer.drawString("Оба игрока подтверждают обмен вручную", 38, 142, TEXT_MUTED);
		}
		fontRenderer.drawString("Любое изменение предметов или отмена сбросят подтверждение", 38, 152, TEXT_MUTED);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.disableTexture2D();
		drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, PANEL_FILL);
		drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + 1, PANEL_GLOW);
		drawRect(guiLeft, guiTop, guiLeft + 1, guiTop + ySize, 0x1853E6FF);
		drawRect(guiLeft + xSize - 1, guiTop, guiLeft + xSize, guiTop + ySize, 0x1853E6FF);
		drawRect(guiLeft, guiTop + ySize - 1, guiLeft + xSize, guiTop + ySize, 0x28000000);
		drawRect(guiLeft + 18, guiTop + 23, guiLeft + xSize - 18, guiTop + 24, 0x1536D4EC);

		drawCard(guiLeft + 18, guiTop + 36, 92, 62);
		drawCard(guiLeft + 168, guiTop + 36, 92, 62);
		drawInventoryCard(guiLeft + 18, guiTop + 160, 242, 73);
		drawCenterGlyph();
		GlStateManager.enableTexture2D();
	}

	private void drawCard(int x, int y, int width, int height) {
		drawRect(x, y, x + width, y + height, CARD_FILL);
		drawRect(x, y, x + width, y + 1, CARD_EDGE);
		drawRect(x, y, x + 1, y + height, 0x1836D4EC);
		drawRect(x + width - 1, y, x + width, y + height, 0x1836D4EC);
		drawRect(x, y + height - 1, x + width, y + height, PANEL_DEEP);
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int sx = x + 10 + col * 18;
				int sy = y + 10 + row * 18;
				drawRect(sx, sy, sx + 16, sy + 16, SLOT_GRID);
			}
		}
	}

	private void drawInventoryCard(int x, int y, int width, int height) {
		drawRect(x, y, x + width, y + height, CARD_FILL);
		drawRect(x, y, x + width, y + 1, CARD_EDGE);
		drawRect(x, y, x + 1, y + height, 0x1436D4EC);
		drawRect(x + width - 1, y, x + width, y + height, 0x1436D4EC);
		drawRect(x, y + height - 1, x + width, y + height, PANEL_DEEP);
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 9; col++) {
				int sx = x + 40 + col * 18;
				int sy = y + 8 + row * 18;
				drawRect(sx, sy, sx + 16, sy + 16, SLOT_GRID);
			}
		}
	}

	private void drawCenterGlyph() {
		int centerX = guiLeft + xSize / 2;
		int centerY = guiTop + 66;
		drawRect(centerX - 20, centerY - 2, centerX + 20, centerY + 2, 0x4063E6FF);
		drawRect(centerX - 2, centerY - 20, centerX + 2, centerY + 20, 0x4063E6FF);
		drawRect(centerX - 30, centerY - 1, centerX - 20, centerY + 1, 0x1E63E6FF);
		drawRect(centerX + 20, centerY - 1, centerX + 30, centerY + 1, 0x1E63E6FF);
	}

	private String statusLabel(boolean ready) {
		return ready ? "готов" : "ожидание";
	}
}
