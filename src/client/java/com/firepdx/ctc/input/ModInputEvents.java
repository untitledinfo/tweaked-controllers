package com.firepdx.ctc.input;

import com.firepdx.ctc.controller.TweakedLinkedControllerClientHandler;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;

/**
 * NeoForge's InputEvent.InteractionKeyMappingTriggered fired for any attack/use key
 * press, regardless of what (if anything) it hit. Fabric API doesn't expose that exact
 * signal, so this listens to the four vanilla interaction callbacks instead — between
 * them they cover the same "the player just pressed attack or use" moments. All four
 * just observe and return PASS; none of them block or change vanilla behavior.
 */
public class ModInputEvents
{
    public static void register()
    {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            TweakedLinkedControllerClientHandler.deactivateInLectern();
            return InteractionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            TweakedLinkedControllerClientHandler.deactivateInLectern();
            return InteractionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            TweakedLinkedControllerClientHandler.deactivateInLectern();
            return InteractionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            TweakedLinkedControllerClientHandler.deactivateInLectern();
            return InteractionResult.PASS;
        });
    }
}
