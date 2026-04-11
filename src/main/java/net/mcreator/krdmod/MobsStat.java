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
    private static final int PANEL_FILL = 0xCC071018;
    private static final int PANEL_GLOW = 0x6600E5FF;
    private static final int TEXT_PRIMARY = 0xEAF7FF;
    private static final int TEXT_SECONDARY = 0x9CBAC7;
    private static final int CYAN_ACCENT = 0x63E6FF;
    private static final int SAFE_ACCENT = 0x66E4A8;
    private static final int DANGER_ACCENT = 0xFF6B6B;

    private static final ResourceLocation CUSTOM_CROSSHAIR = new ResourceLocation("krd_mod", "textures/gui/crosshair.png");
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

                int panelWidth = 154;
                int panelHeight = 56;
                int panelX = sw - panelWidth - 14;
                int panelY = (sh / 2) - 88;

                int hpBarX = panelX + 12;
                int hpBarY = panelY + 23;
                int hpBarW = panelWidth - 24;
                int hpBarH = 9;
                int fillWidth = Math.max(0, Math.min(hpBarW - 2, (int) Math.round((double) hp / Math.max(1, maxHp) * (hpBarW - 2))));
                int hpColor = (target instanceof IMob) ? 0xFF5CE08B : 0xFF41C7FF;

                drawSoftPanel(panelX, panelY, panelWidth, panelHeight);
                Gui.drawRect(panelX + 12, panelY + 18, panelX + panelWidth - 12, panelY + 19, 0x143EDFFF);
                Gui.drawRect(hpBarX, hpBarY, hpBarX + hpBarW, hpBarY + hpBarH, 0x70000000);
                Gui.drawRect(hpBarX + 1, hpBarY + 1, hpBarX + 1 + fillWidth, hpBarY + hpBarH - 1, hpColor);
                Gui.drawRect(hpBarX + 1, hpBarY + 1, hpBarX + 1 + fillWidth, hpBarY + 2, 0x22FFFFFF);

                mc.fontRenderer.drawStringWithShadow(name, panelX + 12, panelY + 8, TEXT_PRIMARY);
                int hpTextWidth = mc.fontRenderer.getStringWidth(hpText);
                mc.fontRenderer.drawStringWithShadow(hpText, panelX + panelWidth - hpTextWidth - 12, panelY + 8, TEXT_PRIMARY);

                String threatText = (target instanceof IMob) ? "[Угроза]" : "[Мирный]";
                int threatColor = (target instanceof IMob) ? DANGER_ACCENT : SAFE_ACCENT;
                mc.fontRenderer.drawStringWithShadow(threatText, panelX + 12, panelY + 38, threatColor);
                String distanceText = distance + "m";
                mc.fontRenderer.drawStringWithShadow(distanceText, panelX + panelWidth - mc.fontRenderer.getStringWidth(distanceText) - 12, panelY + 38, TEXT_SECONDARY);
            }
        }
    }

    private static void drawSoftPanel(int x, int y, int w, int h) {
        Gui.drawRect(x, y, x + w, y + h, PANEL_FILL);
        Gui.drawRect(x, y, x + w, y + 1, PANEL_GLOW);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0x160F2E38);
        Gui.drawRect(x, y, x + 1, y + h, 0x1200DFFF);
        Gui.drawRect(x + w - 1, y, x + w, y + h, 0x1200DFFF);
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
