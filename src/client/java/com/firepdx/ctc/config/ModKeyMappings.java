package com.firepdx.ctc.config;

import com.firepdx.ctc.CreateTweakedControllers;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class ModKeyMappings
{
    public static KeyMapping KEY_MOUSE_FOCUS;
    public static KeyMapping KEY_MOUSE_RESET;
    public static KeyMapping KEY_CONTROLLER_EXIT;

    public static void register()
    {
        KEY_MOUSE_FOCUS = registerKey("mouse_focus", InputConstants.KEY_LALT);
        KEY_MOUSE_RESET = registerKey("mouse_reset", InputConstants.KEY_R);
        KEY_CONTROLLER_EXIT = registerKey("controller_exit", InputConstants.KEY_TAB);
    }

    private static KeyMapping registerKey(String name, int keycode)
    {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
            CreateTweakedControllers.ID + ".keybind." + name,
            InputConstants.Type.KEYSYM,
            keycode,
            CreateTweakedControllers.NAME));
    }
}
