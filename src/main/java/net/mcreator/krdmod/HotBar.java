package net.mcreator.krdmod;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class HotBar extends Gui {

    // ВАЖНО: Замени "твой_modid" на реальный ID твоего мода!
    // Также проверь путь до текстур. В MCreator они обычно лежат в textures/ или textures/gui/
    private static final ResourceLocation SLOT_NORMAL = new ResourceLocation("krd_mod", "textures/gui/interfeisitem.png");
    private static final ResourceLocation SLOT_SELECTED = new ResourceLocation("krd_mod", "textures/gui/interfeisitem1.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        // Проверяем, что сейчас рисуется именно хотбар
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            // Отключаем ванильный хотбар
            event.setCanceled(true);

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            if (player == null) return;

            int screenWidth = event.getResolution().getScaledWidth();
            int screenHeight = event.getResolution().getScaledHeight();

            // Задаем размер твоей иконки слота (допустим, 22x22 пикселя)
            int slotSize = 22; 
            // Вычисляем начальную точку по X, чтобы хотбар был по центру
            int startX = screenWidth / 2 - (9 * slotSize) / 2;
            int y = screenHeight - slotSize - 2;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();

            // 1. Отрисовка фонов для 9 слотов
            for (int i = 0; i < 9; i++) {
                int x = startX + i * slotSize;
                
                // Проверяем, выбран ли текущий слот
                if (player.inventory.currentItem == i) {
                    mc.getTextureManager().bindTexture(SLOT_SELECTED);
                } else {
                    mc.getTextureManager().bindTexture(SLOT_NORMAL);
                }
                
                // Рисуем текстуру. Параметры: x, y, u, v, ширина, высота, ширина текстуры, высота текстуры
                Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, slotSize, slotSize, slotSize, slotSize);
            }

            // 2. Отрисовка слота для левой руки (Offhand)
            ItemStack offhandStack = player.getHeldItemOffhand();
            if (!offhandStack.isEmpty()) {
                int offhandX = startX - slotSize - 10; // Сдвигаем влево от основного хотбара с отступом
                mc.getTextureManager().bindTexture(SLOT_NORMAL);
                Gui.drawModalRectWithCustomSizedTexture(offhandX, y, 0, 0, slotSize, slotSize, slotSize, slotSize);

                // Отрисовка предмета в левой руке
                renderItem(mc, offhandStack, offhandX + 3, y + 3); 
            }

            // 3. Отрисовка самих предметов поверх слотов
            for (int i = 0; i < 9; i++) {
                int x = startX + i * slotSize;
                ItemStack stack = player.inventory.mainInventory.get(i);
                if (!stack.isEmpty()) {
                    // +3 используется для центрирования предмета (он обычно 16x16) внутри слота 22x22
                    renderItem(mc, stack, x + 3, y + 3); 
                }
            }
        }
    }

    // Метод для корректной отрисовки 3D/2D предметов в интерфейсе
    private static void renderItem(Minecraft mc, ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting(); // Включаем свет, чтобы кастомные 3D модели не были черными
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}