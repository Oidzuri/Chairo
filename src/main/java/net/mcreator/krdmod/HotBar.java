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

    // Verify the mod id and texture paths if you move these files later.
    private static final ResourceLocation SLOT_NORMAL = new ResourceLocation("krd_mod", "textures/gui/interfeisitem.png");
    private static final ResourceLocation SLOT_SELECTED = new ResourceLocation("krd_mod", "textures/gui/interfeisitem1.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        // Only replace the vanilla hotbar layer.
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            // Hide vanilla hotbar rendering.
            event.setCanceled(true);

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            if (player == null) return;

            int screenWidth = event.getResolution().getScaledWidth();
            int screenHeight = event.getResolution().getScaledHeight();

            // Custom slot size.
            int slotSize = 22; 
            // Center the bar horizontally.
            int startX = screenWidth / 2 - (9 * slotSize) / 2;
            int y = screenHeight - slotSize - 2;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();

            // 1. Draw slot backgrounds.
            for (int i = 0; i < 9; i++) {
                int x = startX + i * slotSize;
                
                // Highlight the selected slot.
                if (player.inventory.currentItem == i) {
                    mc.getTextureManager().bindTexture(SLOT_SELECTED);
                } else {
                    mc.getTextureManager().bindTexture(SLOT_NORMAL);
                }
                
                // Draw slot texture.
                Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, slotSize, slotSize, slotSize, slotSize);
            }

            // 2. Draw offhand slot.
            ItemStack offhandStack = player.getHeldItemOffhand();
            if (!offhandStack.isEmpty()) {
                int offhandX = startX - slotSize - 10;
                mc.getTextureManager().bindTexture(SLOT_NORMAL);
                Gui.drawModalRectWithCustomSizedTexture(offhandX, y, 0, 0, slotSize, slotSize, slotSize, slotSize);

                renderItem(mc, offhandStack, offhandX + 3, y + 3); 
            }

            // 3. Draw items on top.
            for (int i = 0; i < 9; i++) {
                int x = startX + i * slotSize;
                ItemStack stack = player.inventory.mainInventory.get(i);
                if (!stack.isEmpty()) {
                    renderItem(mc, stack, x + 3, y + 3); 
                }
            }
        }
    }

    // Render a stack with item lighting enabled.
    private static void renderItem(Minecraft mc, ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
