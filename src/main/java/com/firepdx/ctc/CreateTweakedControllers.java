package com.firepdx.ctc;

import com.firepdx.ctc.block.ModBlocks;
import com.firepdx.ctc.compat.ComputerCraft.ModComputerCraftProxy;
import com.firepdx.ctc.item.ModDataComponents;
import com.firepdx.ctc.item.ModItems;
import com.firepdx.ctc.packet.ModPackets;
import com.simibubi.create.Create;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.createmod.catnip.lang.LangBuilder;

/**
 * Fabric port of "Create: Tweaked Controllers" (originally NeoForge, by getItemFromBlock).
 * Forked and ported to Fabric by Firepdx.
 * <p>
 * NOTE ON THIS PORT: the original mod used Create's Registrate helper
 * (CreateRegistrate) for block/item/menu/block-entity registration. This port
 * registers directly against Fabric/vanilla registries instead, since Registrate's
 * Fabric support (if any, for this Create version) couldn't be verified against a
 * real Create-Fabric jar while writing this. Swap back to Registrate if your Create
 * build ships it and you prefer the datagen conveniences it provides.
 */
public class CreateTweakedControllers implements ModInitializer
{
    public static final String ID = "ctc";
    public static final String NAME = "Tweaked Controllers";

    @Override
    public void onInitialize()
    {
        ModItems.register();
        ModBlocks.register();
        ModBlockEntityTypes.register();
        ModDataComponents.register();
        com.firepdx.ctc.menu.ModMenuTypes.register();
        ModTab.register();
        ModPackets.registerCommon();
        ModCommonEvents.register();
        ModComputerCraftProxy.register();
    }

    public static ResourceLocation asResource(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static MutableComponent translateDirect(String key, Object... args)
    {
        return Component.translatable(ID + "." + key, LangBuilder.resolveBuilders(args));
    }

    public static MutableComponent translateDirectRaw(String key, Object... args)
    {
        return Component.translatable(key, LangBuilder.resolveBuilders(args));
    }

    public static LangBuilder builder()
    {
        return new LangBuilder(ID);
    }

    public static LangBuilder translate(String langKey, Object... args)
    {
        return builder().translate(langKey, args);
    }

    public static void log(String message)
    {
        Create.LOGGER.info(message);
    }

    public static void error(String message)
    {
        Create.LOGGER.error(message);
    }
}
