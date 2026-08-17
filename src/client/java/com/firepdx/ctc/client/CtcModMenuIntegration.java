package com.firepdx.ctc.client;

import com.firepdx.ctc.gui.ModConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Optional integration: only loaded if ModMenu is present (declared under the
 * "modmenu" entrypoint key in fabric.mod.json, which ModMenu itself looks for — it's
 * silently ignored if ModMenu isn't installed).
 */
public class CtcModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return ModConfigScreen::new;
    }
}
