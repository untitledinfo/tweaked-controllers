package com.firepdx.ctc.registry;

import com.firepdx.ctc.TweakedControllers;
import com.firepdx.ctc.block.ControllerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Central registry for every block added by Tweaked Controllers.
 * Other mods can reference these directly, or look blocks up dynamically
 * through {@link com.firepdx.ctc.api.TweakedControllersApi}.
 */
public final class ModBlocks {

    private ModBlocks() {
    }

    public static final Block CONTROLLER_BLOCK = register(
            "controller_block",
            settings -> new ControllerBlock(settings),
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .strength(3.5f)
                    .requiresTool()
                    .nonOpaque()
    );

    private static Block register(String path, java.util.function.Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = keyOf(path);
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    private static RegistryKey<Block> keyOf(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(TweakedControllers.MOD_ID, path));
    }

    public static void init() {
        // Referencing this class triggers static init / registration.
        TweakedControllers.LOGGER.info("[{}] Registered blocks", TweakedControllers.MOD_ID);
    }
}
