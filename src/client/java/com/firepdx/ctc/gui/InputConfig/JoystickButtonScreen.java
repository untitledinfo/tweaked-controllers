package com.firepdx.ctc.gui.InputConfig;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.input.JoystickButtonInput;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class JoystickButtonScreen extends GenericInputScreen
{
    public JoystickButtonInput source;
    private Checkbox box;

    public JoystickButtonScreen(Screen parent, Component name, JoystickButtonInput s)
    {
        super(parent, name, s);
        source = s;
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int x, int y, float partialTicks)
    {
        super.renderWindow(graphics, x, y, partialTicks);
        source.invertValue = box.selected();
    }

    @Override
    protected void Populate()
    {
        box = Checkbox.builder(CreateTweakedControllers.translateDirect("gui_config_invert"), this.font)
            .pos(width / 2 - 60, height/2 - 10)
            .selected(source.invertValue)
            .build();
        addRenderableWidget(box);
    }
    
}
