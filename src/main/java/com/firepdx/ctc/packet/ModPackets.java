package com.firepdx.ctc.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class ModPackets
{
    /** Registers payload types + server-side receivers. Call from the common ModInitializer. */
    public static void registerCommon()
    {
        PayloadTypeRegistry.playC2S().register(TweakedLinkedControllerButtonPacket.TYPE, TweakedLinkedControllerButtonPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TweakedLinkedControllerAxisPacket.TYPE, TweakedLinkedControllerAxisPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TweakedLinkedControllerBindPacket.TYPE, TweakedLinkedControllerBindPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TweakedLinkedControllerStopLecternPacket.TYPE, TweakedLinkedControllerStopLecternPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TweakedLinkedControllerClearPacket.TYPE, TweakedLinkedControllerClearPacket.STREAM_CODEC);

        registerServerReceiver(TweakedLinkedControllerButtonPacket.TYPE, TweakedLinkedControllerPacketBase::handle);
        registerServerReceiver(TweakedLinkedControllerAxisPacket.TYPE, TweakedLinkedControllerPacketBase::handle);
        registerServerReceiver(TweakedLinkedControllerBindPacket.TYPE, TweakedLinkedControllerPacketBase::handle);
        registerServerReceiver(TweakedLinkedControllerStopLecternPacket.TYPE, TweakedLinkedControllerPacketBase::handle);
        ServerPlayNetworking.registerGlobalReceiver(TweakedLinkedControllerClearPacket.TYPE,
            (payload, context) -> context.server().execute(() -> TweakedLinkedControllerClearPacket.handle(payload, context.player())));
    }

    private static <T extends TweakedLinkedControllerPacketBase> void registerServerReceiver(
        CustomPacketPayload.Type<T> type, java.util.function.BiConsumer<T, ServerPlayer> handler)
    {
        ServerPlayNetworking.registerGlobalReceiver(type,
            (payload, context) -> context.server().execute(() -> handler.accept(payload, context.player())));
    }

    public static void sendToServer(CustomPacketPayload payload)
    {
        net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.send(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToNear(Level world, BlockPos pos, int range, CustomPacketPayload payload)
    {
        if (!(world instanceof ServerLevel serverLevel))
            return;
        double rangeSq = (double) range * range;
        for (ServerPlayer player : serverLevel.players())
        {
            if (player.blockPosition().distSqr(pos) <= rangeSq)
                ServerPlayNetworking.send(player, payload);
        }
    }
}
