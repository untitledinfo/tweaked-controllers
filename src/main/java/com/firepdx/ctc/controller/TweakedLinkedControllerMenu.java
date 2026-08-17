package com.firepdx.ctc.controller;

import com.firepdx.ctc.item.TweakedLinkedControllerItem;
import com.firepdx.ctc.menu.ControllerItemSlot;
import com.firepdx.ctc.menu.ModMenuTypes;
import com.firepdx.ctc.packet.ModPackets;
import com.firepdx.ctc.packet.TweakedLinkedControllerClearPacket;
import com.firepdx.ctc.util.SimpleItemStackHandler;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Rewritten off Create's GhostItemMenu (NeoForge-only foundation class) onto a plain
 * AbstractContainerMenu, since this port couldn't verify whether Create-Fabric ships an
 * equivalent. Behavior should match: 50 "ghost" frequency slots backed by
 * SimpleItemStackHandler, persisted onto the held controller item's data component.
 */
public class TweakedLinkedControllerMenu extends AbstractContainerMenu
{
    public final Inventory playerInventory;
    public final Player player;
    public final ItemStack contentHolder;
    public final SimpleItemStackHandler ghostInventory;

    private boolean isSecondPage = false;

    private TweakedLinkedControllerMenu(int id, Inventory inv, ItemStack filterItem)
    {
        super(ModMenuTypes.TWEAKED_LINKED_CONTROLLER.get(), id);
        this.playerInventory = inv;
        this.player = inv.player;
        this.contentHolder = filterItem;
        this.ghostInventory = TweakedLinkedControllerItem.getFrequencyItems(contentHolder);
        addSlots();
        addPlayerSlots(32, 194);
    }

    public static TweakedLinkedControllerMenu create(int id, Inventory inv, ItemStack filterItem)
    {
        return new TweakedLinkedControllerMenu(id, inv, filterItem);
    }

    public void SetPage(boolean second)
    {
        isSecondPage = second;
        int slotIndex = this.slots.size() - 50;
        for (int r = 0; r < 2; r++)
        {
            boolean isVisible = (isSecondPage && r == 1) || (!isSecondPage && r == 0);
            for (int index = 0; index < guiItemSlots[r].length; index += 2)
            {
                for (int row = 0; row < 2; ++row)
                {
                    ControllerItemSlot t = (ControllerItemSlot) (this.slots.get(slotIndex));
                    t.SetActive(isVisible);
                    slotIndex++;
                }
            }
        }
    }

    /** Clears every ghost slot locally and asks the server to do the same. */
    public void clearContents()
    {
        ghostInventory.clearContent();
    }

    public void sendClearPacket()
    {
        ModPackets.sendToServer(new TweakedLinkedControllerClearPacket());
    }

    protected static final int[][] guiItemSlots =
    {
        {
            36, 34,
            84, 34,
            60, 34,
            12, 34,
            167, 97,
            191, 97,
            131, 34,
            155, 34,
            179, 34,
            119, 97,
            143, 97,
            12, 97,
            84, 97,
            36, 97,
            60, 97
        },
        {
            48, 34,
            72, 34,
            96, 34,
            120, 34,
            48, 97,
            72, 97,
            96, 97,
            120, 97,
            191, 34,
            191, 97
        }
    };

    protected void addSlots()
    {
        int slot = 0;
        for (int r = 0; r < 2; r++)
        {
            boolean isVisible = (isSecondPage && r == 1) || (!isSecondPage && r == 0);
            for (int index = 0; index < guiItemSlots[r].length; index += 2)
            {
                int x = guiItemSlots[r][index];
                int y = guiItemSlots[r][index + 1];
                for (int row = 0; row < 2; ++row)
                    addSlot(new ControllerItemSlot(ghostInventory, slot++, x, y + row * 18, isVisible));
            }
        }
    }

    protected void addPlayerSlots(int x, int y)
    {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, x + col * 18, y + 58));
    }

    protected void saveData(ItemStack holder)
    {
        TweakedLinkedControllerItem.setFrequencyItems(holder, ghostInventory);
    }

    @Override
    public void removed(Player player)
    {
        if (!player.level().isClientSide)
            saveData(contentHolder);
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        // Ghost slots aren't a real backing inventory to shift-click items into/out of;
        // only allow the usual player-inventory <-> hotbar shift-click behavior.
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem() || slot.container != playerInventory)
            return ItemStack.EMPTY;

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        int playerSlotsStart = slots.size() - 36;
        int hotbarStart = slots.size() - 9;

        boolean movedToHotbar;
        if (index < playerSlotsStart + 27)
            movedToHotbar = moveItemStackTo(original, hotbarStart, slots.size(), false);
        else
            movedToHotbar = moveItemStackTo(original, playerSlotsStart, playerSlotsStart + 27, false);

        if (!movedToHotbar)
            return ItemStack.EMPTY;

        if (original.isEmpty())
            slot.set(ItemStack.EMPTY);
        else
            slot.setChanged();

        return copy;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player)
    {
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return playerInventory.getSelected() == contentHolder;
    }
}
