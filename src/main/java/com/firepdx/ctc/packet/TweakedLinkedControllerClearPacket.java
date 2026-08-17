package com.firepdx.ctc.packet;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.item.ModItems;
import com.firepdx.ctc.item.TweakedLinkedControllerItem;
import com.firepdx.ctc.util.SimpleItemStackHandler;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * New packet (not present in the original mod): clears every frequency slot on the
 * player's held controller. Added because the original relied on Create's
 * GhostItemMenu#sendClearPacket(), which this Fabric port doesn't carry over — see
 * TweakedLinkedControllerMenu for where this is sent from.
 */
public record TweakedLinkedControllerClearPacket() implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<TweakedLinkedControllerClearPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CreateTweakedControllers.ID, "clear"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TweakedLinkedControllerClearPacket> STREAM_CODEC =
        StreamCodec.unit(new TweakedLinkedControllerClearPacket());

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(TweakedLinkedControllerClearPacket packet, ServerPlayer player)
    {
        for (InteractionHand hand : InteractionHand.values())
        {
            ItemStack stack = player.getItemInHand(hand);
            if (ModItems.TWEAKED_LINKED_CONTROLLER.isIn(stack))
            {
                TweakedLinkedControllerItem.setFrequencyItems(stack,
                    new SimpleItemStackHandler(TweakedLinkedControllerItem.FREQUENCY_SLOTS));
            }
        }
    }
}
