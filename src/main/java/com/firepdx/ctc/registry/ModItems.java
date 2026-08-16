package com.firepdx.ctc.registry;

import com.firepdx.ctc.TweakedControllers;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {

    private ModItems() {
    }

    public static final Item CONTROLLER_BLOCK_ITEM = registerBlockItem(
            "controller_block",
            ModBlocks.CONTROLLER_BLOCK
    );

    private static Item registerBlockItem(String path, net.minecraft.block.Block block) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(TweakedControllers.MOD_ID, path));
        Item item = Registry.register(
                Registries.ITEM,
                key,
                new BlockItem(block, new Item.Settings().registryKey(key))
        );
        return item;
    }

    public static void init() {
        ItemGroupEvents();
        TweakedControllers.LOGGER.info("[{}] Registered items", TweakedControllers.MOD_ID);
    }

    private static void ItemGroupEvents() {
        net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE)
                .register(entries -> entries.add(CONTROLLER_BLOCK_ITEM));
    }
}
