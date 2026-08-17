package com.firepdx.ctc;

import com.firepdx.ctc.item.ItemDisplay;
import com.firepdx.ctc.item.ModItems;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModTab
{
    public static final ResourceKey<CreativeModeTab> BASE_KEY =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateTweakedControllers.asResource("base"));

    public static void register()
    {
        CreativeModeTab tab = CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + CreateTweakedControllers.ID + ".base"))
            .icon(() -> new ItemStack(ModItems.TWEAKED_LINKED_CONTROLLER))
            .displayItems(new ItemDisplay.ItemDisplayImpl())
            .build();

        net.minecraft.core.Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, BASE_KEY.location(), tab);
    }
}
