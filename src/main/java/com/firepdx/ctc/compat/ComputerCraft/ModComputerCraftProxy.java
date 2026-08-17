package com.firepdx.ctc.compat.ComputerCraft;

import java.util.function.Function;

import com.firepdx.ctc.ModBlockEntityTypes;
import com.firepdx.ctc.block.TweakedLecternControllerBlockEntity;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.FallbackComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.fabricmc.loader.api.FabricLoader;

public class ModComputerCraftProxy
{
    private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
    private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

    public static void register()
    {
        fallbackFactory = FallbackComputerBehaviour::new;
        if (FabricLoader.getInstance().isModLoaded("computercraft"))
        {
            computerFactory = ModComputerBehavior::new;
            registerPeripheral();
        }
    }

    public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe)
    {
        if (computerFactory == null)
            return fallbackFactory.apply(sbe);
        return computerFactory.apply(sbe);
    }

    /**
     * UNVERIFIED: CC:Tweaked-Fabric exposes peripheral registration through a
     * BlockApiLookup-style class (dan200.computercraft.api.peripheral.PeripheralLookup
     * on recent versions), rather than NeoForge's RegisterCapabilitiesEvent. Confirm the
     * exact class/method name against the CC:Tweaked-Fabric version you actually depend
     * on and adjust this method — the shape below is the closest analog available.
     */
    private static void registerPeripheral()
    {
        dan200.computercraft.api.peripheral.PeripheralLookup.get().registerForBlockEntity(
            (blockEntity, side) -> {
                if (blockEntity instanceof TweakedLecternControllerBlockEntity lectern)
                    return lectern.computerBehaviour.getPeripheralCapability();
                return null;
            },
            ModBlockEntityTypes.TWEAKED_LECTERN_CONTROLLER.get());
    }
}
