package com.firepdx.ctc.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

/**
 * Common-code slot (menus run on both logical sides). Previously extended NeoForge's
 * SlotItemHandler; now a plain vanilla Slot over our Container-backed SimpleItemStackHandler.
 */
public class ControllerItemSlot extends Slot
{
    protected boolean active = true;

    public ControllerItemSlot(Container container, int index, int xPosition, int yPosition)
    {
        super(container, index, xPosition, yPosition);
    }

    public ControllerItemSlot(Container container, int index, int xPosition, int yPosition, boolean active)
    {
        super(container, index, xPosition, yPosition);
        this.active = active;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    public void SetActive(boolean active)
    {
        this.active = active;
    }
}
