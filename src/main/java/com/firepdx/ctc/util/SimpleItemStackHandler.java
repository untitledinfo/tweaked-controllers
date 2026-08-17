package com.firepdx.ctc.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal stand-in for NeoForge's {@code ItemStackHandler}, used for the controller's
 * "ghost" frequency slots. Implements vanilla {@link Container} directly so it can back
 * ordinary {@link net.minecraft.world.inventory.Slot}s in the menu, avoiding any
 * dependency on NeoForge's item-handler capability system (no Fabric equivalent).
 */
public class SimpleItemStackHandler implements Container
{
    private final List<ItemStack> stacks;

    public SimpleItemStackHandler(int size)
    {
        stacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            stacks.add(ItemStack.EMPTY);
    }

    public int getSlots()
    {
        return stacks.size();
    }

    public ItemStack getStackInSlot(int slot)
    {
        return slot >= 0 && slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
    }

    public void setStackInSlot(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < stacks.size())
            stacks.set(slot, stack == null ? ItemStack.EMPTY : stack);
    }

    /** Builds an {@link ItemContainerContents} snapshot of the current slots. */
    public ItemContainerContents toContainerContents()
    {
        return ItemContainerContents.fromItems(stacks);
    }

    /** Fills a new handler's slots from a previously-saved {@link ItemContainerContents}. */
    public static SimpleItemStackHandler fromContainerContents(int size, ItemContainerContents contents)
    {
        SimpleItemStackHandler handler = new SimpleItemStackHandler(size);
        // stream() preserves slot positions (including empty ones); nonEmptyItems() does not.
        List<ItemStack> items = contents.stream().toList();
        for (int i = 0; i < size && i < items.size(); i++)
            handler.setStackInSlot(i, items.get(i));
        return handler;
    }

    // ---- net.minecraft.world.Container ----

    @Override
    public int getContainerSize()
    {
        return stacks.size();
    }

    @Override
    public boolean isEmpty()
    {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount)
    {
        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty())
            return ItemStack.EMPTY;
        ItemStack split = current.split(amount);
        setChanged();
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        ItemStack current = getStackInSlot(slot);
        setStackInSlot(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
        setStackInSlot(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged()
    {
        // No-op: persistence happens explicitly via TweakedLinkedControllerItem#setFrequencyItems.
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < stacks.size(); i++)
            stacks.set(i, ItemStack.EMPTY);
        setChanged();
    }
}
