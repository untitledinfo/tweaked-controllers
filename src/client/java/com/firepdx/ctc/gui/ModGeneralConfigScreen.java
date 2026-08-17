package com.firepdx.ctc.gui;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.config.ModClientConfig;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * New addition (not in the original mod): a minimal settings list standing in for
 * Create/Catnip's SubMenuConfigScreen, which read a NeoForge ModConfigSpec directly.
 * Since ModClientConfig here is a small JSON-backed config instead, this just lists
 * each value with a button that cycles/toggles it.
 */
public class ModGeneralConfigScreen extends AbstractSimiScreen
{
    private final Screen parent;

    public ModGeneralConfigScreen(Screen parent)
    {
        this.parent = parent;
    }

    @Override
    protected void init()
    {
        super.init();
        int center = width / 2;
        int y = height / 4 + 20;
        int rowHeight = 24;

        y = addToggleRow(y, center, "toggle_mouse_focus", ModClientConfig.TOGGLE_MOUSE_FOCUS, rowHeight);
        y = addToggleRow(y, center, "auto_reset_mouse_focus", ModClientConfig.AUTO_RESET_MOUSE_FOCUS, rowHeight);
        y = addIntRow(y, center, "config_button_main_menu_row", ModClientConfig.CONFIG_BUTTON_MAIN_MENU_ROW, 0, 4, rowHeight);
        y = addIntRow(y, center, "config_button_main_menu_offset", ModClientConfig.CONFIG_BUTTON_MAIN_MENU_OFFSET, -32, 32, rowHeight);
        y = addIntRow(y, center, "config_button_ingame_menu_row", ModClientConfig.CONFIG_BUTTON_INGAME_MENU_ROW, 0, 5, rowHeight);
        y = addIntRow(y, center, "config_button_ingame_menu_offset", ModClientConfig.CONFIG_BUTTON_INGAME_MENU_OFFSET, -32, 32, rowHeight);
        addLayoutRow(y, center, rowHeight);

        addRenderableWidget(Button.builder(CreateTweakedControllers.translateDirect("menu.return"), $ -> {
            ScreenOpener.open(parent);
        }).bounds(center - 100, height - 32, 200, 20).build());
    }

    private int addToggleRow(int y, int center, String key, ModClientConfig.ConfigValue<Boolean> value, int rowHeight)
    {
        addRenderableWidget(Button.builder(rowLabel(key, value.get()), b -> {
            value.set(!value.get());
            b.setMessage(rowLabel(key, value.get()));
        }).bounds(center - 100, y, 200, 20).build());
        return y + rowHeight;
    }

    private int addIntRow(int y, int center, String key, ModClientConfig.ConfigValue<Integer> value, int min, int max, int rowHeight)
    {
        addRenderableWidget(Button.builder(rowLabel(key, value.get()), b -> {
            int next = value.get() + 1;
            if (next > max) next = min;
            value.set(next);
            b.setMessage(rowLabel(key, value.get()));
        }).bounds(center - 100, y, 200, 20).build());
        return y + rowHeight;
    }

    private void addLayoutRow(int y, int center, int rowHeight)
    {
        addRenderableWidget(Button.builder(rowLabel("controller_layout_type", ModClientConfig.CONTROLLER_LAYOUT_TYPE.get()), b -> {
            ModClientConfig.ControllerLayoutType[] values = ModClientConfig.ControllerLayoutType.values();
            int next = (ModClientConfig.CONTROLLER_LAYOUT_TYPE.get().ordinal() + 1) % values.length;
            ModClientConfig.CONTROLLER_LAYOUT_TYPE.set(values[next]);
            b.setMessage(rowLabel("controller_layout_type", ModClientConfig.CONTROLLER_LAYOUT_TYPE.get()));
        }).bounds(center - 100, y, 200, 20).build());
    }

    private Component rowLabel(String key, Object value)
    {
        return Component.literal(key + ": " + value);
    }

    @Override
    protected void renderWindow(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {}

    public boolean isPauseScreen()
    {
        return true;
    }
}
