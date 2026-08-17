package com.firepdx.ctc.item;

import java.util.function.UnaryOperator;

import com.firepdx.ctc.CreateTweakedControllers;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ItemContainerContents;

public class ModDataComponents
{
    public static final DataComponentType<ItemContainerContents> TWEAKED_CONTROLLER_ITEMS = register(
        "tweaked_controller_items",
        builder -> builder
            .persistent(ItemContainerContents.CODEC)
            .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder)
    {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CreateTweakedControllers.asResource(name), type);
        return type;
    }

    public static void register() {}
}
