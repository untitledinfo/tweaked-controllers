package com.firepdx.ctc.client;

import com.firepdx.ctc.TweakedControllers;
import com.firepdx.ctc.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class TweakedControllersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONTROLLER_BLOCK, RenderLayer.getCutout());
        TweakedControllers.LOGGER.info("[{}] Client init complete", TweakedControllers.MOD_ID);
    }
}
