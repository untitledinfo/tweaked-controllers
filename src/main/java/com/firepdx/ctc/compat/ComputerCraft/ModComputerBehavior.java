package com.firepdx.ctc.compat.ComputerCraft;

import java.util.function.Supplier;

import com.firepdx.ctc.block.TweakedLecternControllerBlockEntity;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dan200.computercraft.api.peripheral.IPeripheral;

public class ModComputerBehavior extends AbstractComputerBehaviour
{
    private IPeripheral peripheral;
    private final Supplier<IPeripheral> peripheralSupplier;
    private final SmartBlockEntity be;

    public ModComputerBehavior(SmartBlockEntity te)
    {
        super(te);
        this.peripheralSupplier = getPeripheralFor(te);
        this.be = te;
    }

    public static Supplier<IPeripheral> getPeripheralFor(SmartBlockEntity be)
    {
        if (be instanceof TweakedLecternControllerBlockEntity tlcbe)
            return () -> new TweakedLecternPeripheral(tlcbe);

        throw new IllegalArgumentException("No peripheral available for " + be.getType()
            .getClass().getName());
    }

    @Override
    public IPeripheral getPeripheralCapability()
    {
        if (peripheral == null)
            peripheral = peripheralSupplier.get();
        return peripheral;
    }

    @Override
    public void removePeripheral()
    {
        // NeoForge's capability-invalidation call (getWorld().invalidateCapabilities(pos))
        // has no Fabric equivalent needed here: PeripheralLookup re-queries the block
        // entity on demand rather than caching a capability instance, so there's nothing
        // to invalidate on this side. Left as a no-op.
    }
}
