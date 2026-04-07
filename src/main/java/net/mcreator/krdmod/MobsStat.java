package net.mcreator.krdmod;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class MobsStat extends Gui {

    private static final ResourceLocation CUSTOM_CROSSHAIR = new ResourceLocation("krd_mod", "textures/gui/crosshair.png");
    private static final ResourceLocation MOB_HP_BAR_LAYER = new ResourceLocation("krd_mod", "textures/gui/layer_for_mob_bar.png");
    private static final ResourceLocation XP_BAR_EMPTY = new ResourceLocation("krd_mod", "textures/gui/xpbarpustxp.png");

    @SubscribeEvent
    public static void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true); 
            Minecraft mc = Minecraft.getMinecraft();
            int sw = event.getResolution().getScaledWidth();
            int sh = event.getResolution().getScaledHeight();
            int size = 4;
            GlStateManager.enableAlpha();
            mc.getTextureManager().bindTexture(CUSTOM_CROSSHAIR);
            Gui.drawModalRectWithCustomSizedTexture((sw - size) / 2, (sh - size) / 2, 0, 0, size, size, size, size);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Minecraft mc = Minecraft.getMinecraft();
            
            // Увеличиваем радиус до 10 блоков с помощью кастомного поиска
            Entity targetEntity = getMouseOverEntity(50.0D);

            if (targetEntity instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) targetEntity;

                int hp = (int) Math.ceil(target.getHealth());
                int maxHp = (int) target.getMaxHealth();
                String name = target.getName();
                String hpText = hp + " / " + maxHp;
                double distance = Math.round(mc.player.getDistance(target) * 10.0) / 10.0;
                String threat = (target instanceof IMob) ? "§c[Угроза]" : "§a[Мирный]";

                int sw = event.getResolution().getScaledWidth();
                int sh = event.getResolution().getScaledHeight();
                
                // Настройки позиции (подняли выше: (sh / 2) - 80)
                int barWidth = 120;
                int barHeight = 45;
                int barX = sw - barWidth - 10;
                int barY = (sh / 2) - 80; 

                // 1. Основная подложка
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(MOB_HP_BAR_LAYER);
                Gui.drawModalRectWithCustomSizedTexture(barX, barY, 0, 0, barWidth, barHeight, barWidth, barHeight);

                // 2. Рамка для полоски ХП (xpbarpustxp.png)
                int xpBarW = 100; // Ширина рамки
                int xpBarH = 12;  // Высота рамки
                int xpBarX = barX + 10;
                int xpBarY = barY + 18;
                mc.getTextureManager().bindTexture(XP_BAR_EMPTY);
                Gui.drawModalRectWithCustomSizedTexture(xpBarX, xpBarY, 0, 0, xpBarW, xpBarH, xpBarW, xpBarH);

                // 3. Зеленая заливка внутри рамки
                int innerMargin = 2; // Отступ, чтобы полоска была ВНУТРИ рамки
                int maxFillWidth = xpBarW - (innerMargin * 2);
                int currentFillWidth = (int) ((double) hp / maxHp * maxFillWidth);
                if (currentFillWidth < 0) currentFillWidth = 0;

                // Рисуем саму полоску
                Gui.drawRect(xpBarX + innerMargin, xpBarY + innerMargin, 
                             xpBarX + innerMargin + currentFillWidth, xpBarY + xpBarH - innerMargin, 0xFF2ECC71);

                // 4. Текст
                mc.fontRenderer.drawStringWithShadow(name, barX + 10, barY + 6, 0xFFFFAA);
                int tw = mc.fontRenderer.getStringWidth(hpText);
                mc.fontRenderer.drawStringWithShadow(hpText, barX + barWidth - tw - 10, barY + 6, 0xFFFFFF);
                mc.fontRenderer.drawStringWithShadow(threat + " §7" + distance + "m", barX + 10, barY + 32, 0xFFFFFF);
            }
        }
    }

    /**
     * Метод для поиска сущности на дистанции больше стандартной
     */
    private static Entity getMouseOverEntity(double distance) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity != null && mc.world != null) {
            RayTraceResult objectMouseOver = viewEntity.rayTrace(distance, 1.0F);
            Vec3d eyePos = viewEntity.getPositionEyes(1.0F);
            double d1 = distance;

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(eyePos);
            }

            Vec3d lookVec = viewEntity.getLook(1.0F);
            Vec3d reachVec = eyePos.addVector(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
            Entity pointedEntity = null;
            List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(viewEntity, 
                    viewEntity.getEntityBoundingBox().expand(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance).grow(1.0D, 1.0D, 1.0D));
            double d2 = d1;

            for (Entity entity : list) {
                if (entity.canBeCollidedWith()) {
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double) entity.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyePos, reachVec);

                    if (axisalignedbb.contains(eyePos)) {
                        if (d2 >= 0.0D) {
                            pointedEntity = entity;
                            d2 = 0.0D;
                        }
                    } else if (raytraceresult != null) {
                        double d3 = eyePos.distanceTo(raytraceresult.hitVec);
                        if (d3 < d2 || d2 == 0.0D) {
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
            return pointedEntity;
        }
        return null;
    }
}