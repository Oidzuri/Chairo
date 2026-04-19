package net.mcreator.krdmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = "krd_mod", value = Side.CLIENT)
public class KRDChatSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final List<SettingRow> rows = new ArrayList<>();

    private static final class SettingRow {
        private final String title;
        private final float min;
        private final float max;
        private final float step;
        private final ValueGetter getter;
        private final ValueSetter setter;
        private GuiButton minusButton;
        private GuiButton plusButton;

        private SettingRow(String title, float min, float max, float step, ValueGetter getter, ValueSetter setter) {
            this.title = title;
            this.min = min;
            this.max = max;
            this.step = step;
            this.getter = getter;
            this.setter = setter;
        }
    }

    private interface ValueGetter {
        float get(Minecraft mc);
    }

    private interface ValueSetter {
        void set(float value);
    }

    public KRDChatSettingsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof ScreenChatOptions) {
            event.setGui(new KRDChatSettingsScreen(Minecraft.getMinecraft().currentScreen));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        rows.clear();
        KRDChatSettings.ensureLoaded(mc);

        rows.add(new SettingRow("Общий масштаб", 0.75F, 1.75F, 0.05F, KRDChatSettings::getUiScale, KRDChatSettings::setUiScale));
        rows.add(new SettingRow("Ширина чата", 0.65F, 1.75F, 0.05F, KRDChatSettings::getWidthScale, KRDChatSettings::setWidthScale));
        rows.add(new SettingRow("Высота чата", 0.70F, 1.75F, 0.05F, KRDChatSettings::getHeightScale, KRDChatSettings::setHeightScale));
        rows.add(new SettingRow("Прозрачность обычного", 0.15F, 1.00F, 0.05F, KRDChatSettings::getCompactOpacity, KRDChatSettings::setCompactOpacity));
        rows.add(new SettingRow("Прозрачность открытого", 0.15F, 1.00F, 0.05F, KRDChatSettings::getExpandedOpacity, KRDChatSettings::setExpandedOpacity));

        int baseX = width / 2 - 110;
        int y = height / 4 + 28;
        for (int i = 0; i < rows.size(); i++) {
            SettingRow row = rows.get(i);
            row.minusButton = new GuiButton(100 + i, baseX, y, 20, 20, "-");
            row.plusButton = new GuiButton(200 + i, baseX + 200, y, 20, 20, "+");
            buttonList.add(row.minusButton);
            buttonList.add(row.plusButton);
            y += 24;
        }

        buttonList.add(new GuiButton(1, width / 2 - 100, height - 38, 98, 20, "Готово"));
        buttonList.add(new GuiButton(2, width / 2 + 2, height - 38, 98, 20, "Сбросить"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            KRDChatSettings.save(mc);
            mc.displayGuiScreen(parent);
            return;
        }
        if (button.id == 2) {
            KRDChatSettings.setUiScale(1.0F);
            KRDChatSettings.setWidthScale(1.0F);
            KRDChatSettings.setHeightScale(1.0F);
            KRDChatSettings.setCompactOpacity(0.56F);
            KRDChatSettings.setExpandedOpacity(0.75F);
            KRDChatSettings.save(mc);
            return;
        }

        if (button.id >= 100 && button.id < 100 + rows.size()) {
            adjust(rows.get(button.id - 100), -1.0F);
            return;
        }
        if (button.id >= 200 && button.id < 200 + rows.size()) {
            adjust(rows.get(button.id - 200), 1.0F);
        }
    }

    private void adjust(SettingRow row, float direction) {
        float current = row.getter.get(mc);
        float next = current + row.step * direction;
        next = Math.max(row.min, Math.min(row.max, next));
        row.setter.set(next);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Настройки кастомного чата", width / 2, height / 4, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Только нужные параметры для KRD chat overlay", width / 2, height / 4 + 12, 0xA0A0A0);

        int labelX = width / 2 - 82;
        int y = height / 4 + 34;
        for (SettingRow row : rows) {
            drawString(fontRenderer, row.title, labelX, y + 6, 0xE0E0E0);
            String value = String.format(Locale.ROOT, "%.2f", row.getter.get(mc));
            drawCenteredString(fontRenderer, value, width / 2 + 55, y + 6, 0x63E6FF);
            y += 24;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        KRDChatSettings.save(mc);
        super.onGuiClosed();
    }
}
