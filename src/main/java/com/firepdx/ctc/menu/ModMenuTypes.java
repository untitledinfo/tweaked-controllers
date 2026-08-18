package com.firepdx.ctc.menu;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.controller.TweakedLinkedControllerMenu;
import com.firepdx.ctc.util.RegistryEntries.MenuTypeEntry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Common-side menu type registration. The screen (client GUI) is registered
 * separately in com.firepdx.ctc.client.CreateTweakedControllersClient, since
 * screens can't be referenced from common code.
 */
public class ModMenuTypes
{
    public static final MenuTypeEntry<ExtendedScreenHandlerType<TweakedLinkedControllerMenu, ItemStack>> TWEAKED_LINKED_CONTROLLER = registerType();

    private static MenuTypeEntry<ExtendedScreenHandlerType<TweakedLinkedControllerMenu, ItemStack>> registerType()
    {
        ResourceLocation id = CreateTweakedControllers.asResource("tweaked_linked_controller");
        ExtendedScreenHandlerType<TweakedLinkedControllerMenu, ItemStack> type = new ExtendedScreenHandlerType<>(
            (syncId, inventory, filterItem) -> TweakedLinkedControllerMenu.create(syncId, inventory, filterItem),
            ItemStack.STREAM_CODEC
        );
        Registry.register(BuiltInRegistries.MENU, id, type);
        return new MenuTypeEntry<>(type);
    }

    public static void register() {}
}
