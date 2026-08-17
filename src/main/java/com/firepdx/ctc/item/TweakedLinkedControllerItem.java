package com.firepdx.ctc.item;

import com.firepdx.ctc.block.ModBlocks;
import com.firepdx.ctc.controller.TweakedLinkedControllerMenu;
import com.firepdx.ctc.util.SimpleItemStackHandler;

import net.createmod.catnip.data.Couple;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Client-only concerns (toggling the active/bind overlay, the custom item renderer)
 * are handled via {@link ClientBindings} and BuiltinItemRendererRegistry respectively —
 * see com.firepdx.ctc.client.CreateTweakedControllersClient.
 */
public class TweakedLinkedControllerItem extends Item implements MenuProvider, ExtendedScreenHandlerFactory<ItemStack>
{
    public static final int FREQUENCY_SLOTS = 50;

    public TweakedLinkedControllerItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
    {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild())
        {
            if (player.isShiftKeyDown())
            {
                if (ModBlocks.TWEAKED_LECTERN_CONTROLLER.has(hitState))
                {
                    if (!world.isClientSide)
                        ModBlocks.TWEAKED_LECTERN_CONTROLLER.get().withBlockEntityDo(world, pos, be ->
                                be.swapControllers(stack, player, ctx.getHand(), hitState));
                    return InteractionResult.SUCCESS;
                }
            }
            else
            {
                if (AllBlocks.REDSTONE_LINK.has(hitState))
                {
                    if (world.isClientSide)
                        ClientBindings.toggleBindMode.accept(ctx.getClickedPos());
                    player.getCooldowns()
                            .addCooldown(this, 2);
                    return InteractionResult.SUCCESS;
                }

                if (hitState.is(Blocks.LECTERN) && !hitState.getValue(LecternBlock.HAS_BOOK))
                {
                    if (!world.isClientSide)
                    {
                        ItemStack lecternStack = player.isCreative() ? stack.copy() : stack.split(1);
                        ModBlocks.TWEAKED_LECTERN_CONTROLLER.get().replaceLectern(hitState, world, pos, lecternStack);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (ModBlocks.TWEAKED_LECTERN_CONTROLLER.has(hitState))
                    return InteractionResult.PASS;
            }
        }

        return use(world, player, ctx.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack heldItem = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND)
        {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer && player.mayBuild())
                serverPlayer.openMenu(this);
            return InteractionResultHolder.success(heldItem);
        }

        if (!player.isShiftKeyDown())
        {
            if (world.isClientSide)
                ClientBindings.toggleActive.run();
            player.getCooldowns()
                .addCooldown(this, 2);
        }

        return InteractionResultHolder.pass(heldItem);
    }

    public static SimpleItemStackHandler getFrequencyItems(ItemStack stack)
    {
        if (!ModItems.TWEAKED_LINKED_CONTROLLER.isIn(stack))
            throw new IllegalArgumentException("Cannot get frequency items from non-controller: " + stack);
        if (!stack.has(ModDataComponents.TWEAKED_CONTROLLER_ITEMS))
            return new SimpleItemStackHandler(FREQUENCY_SLOTS);
        return SimpleItemStackHandler.fromContainerContents(FREQUENCY_SLOTS,
            stack.getOrDefault(ModDataComponents.TWEAKED_CONTROLLER_ITEMS, ItemContainerContents.EMPTY));
    }

    public static void setFrequencyItems(ItemStack stack, SimpleItemStackHandler handler)
    {
        stack.set(ModDataComponents.TWEAKED_CONTROLLER_ITEMS, handler.toContainerContents());
    }

    public static Couple<Frequency> toFrequency(ItemStack controller, int slot)
    {
        SimpleItemStackHandler frequencyItems = getFrequencyItems(controller);
        return Couple.create(Frequency.of(frequencyItems.getStackInSlot(slot * 2)),
            Frequency.of(frequencyItems.getStackInSlot(slot * 2 + 1)));
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player)
    {
        ItemStack heldItem = player.getMainHandItem();
        return TweakedLinkedControllerMenu.create(id, inv, heldItem);
    }

    @Override
    public Component getDisplayName()
    {
        return getDescription();
    }

    /** Supplies the extra data Fabric writes to the client when this screen opens. */
    @Override
    public ItemStack getScreenOpeningData(ServerPlayer player)
    {
        return player.getMainHandItem();
    }
}
