package com.firepdx.ctc.item;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.block.ModBlocks;
import com.firepdx.ctc.util.RegistryEntries.ItemEntry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems
{
    public static final ItemEntry<TweakedLinkedControllerItem> TWEAKED_LINKED_CONTROLLER = register(
        "tweaked_linked_controller",
        new TweakedLinkedControllerItem(new Item.Properties().stacksTo(1))
    );

    public static final ItemEntry<BlockItem> TWEAKED_LECTERN_CONTROLLER_ITEM = register(
        "tweaked_lectern_controller",
        new BlockItem(ModBlocks.TWEAKED_LECTERN_CONTROLLER.get(), new Item.Properties())
    );

    private static <T extends Item> ItemEntry<T> register(String path, T item)
    {
        ResourceLocation id = CreateTweakedControllers.asResource(path);
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return new ItemEntry<>(item);
    }

    public static void register() {}
}
