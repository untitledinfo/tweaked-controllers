package com.firepdx.ctc;

import com.firepdx.ctc.controller.TweakedLinkedControllerServerHandler;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.player.Player;

public class ModCommonEvents
{
    public static void register()
    {
        ServerTickEvents.END_WORLD_TICK.register(world ->
            TweakedLinkedControllerServerHandler.tick(world));

        // Extra check in case of a crash when the player was using a lectern controller
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof Player player)
            {
                if (player.getPersistentData().contains("IsUsingLecternController"))
                {
                    player.getPersistentData().remove("IsUsingLecternController");
                }
            }
        });
    }
}
