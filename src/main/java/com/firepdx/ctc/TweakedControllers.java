package com.firepdx.ctc;

import com.firepdx.ctc.api.ModSupportRegistry;
import com.firepdx.ctc.registry.ModBlocks;
import com.firepdx.ctc.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TweakedControllers implements ModInitializer {

    public static final String MOD_ID = "ctc";
    public static final Logger LOGGER = LoggerFactory.getLogger("Tweaked Controllers");

    @Override
    public void onInitialize() {
        LOGGER.info("[{}] Initializing Tweaked Controllers", MOD_ID);

        ModBlocks.init();
        ModItems.init();

        // Let any installed compat mods register their controller behavior
        // now that our own registries/API are ready.
        ModSupportRegistry.dispatchAll();

        LOGGER.info("[{}] Tweaked Controllers ready ({} mod support bridge(s) loaded)",
                MOD_ID, ModSupportRegistry.getSupportedModIds().size());
    }
}
