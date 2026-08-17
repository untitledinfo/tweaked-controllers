package com.firepdx.ctc.client;

import com.firepdx.ctc.block.ModBlocks;
import com.firepdx.ctc.config.ModClientConfig;
import com.firepdx.ctc.config.ModKeyMappings;
import com.firepdx.ctc.controller.TweakedLecternControllerRenderer;
import com.firepdx.ctc.controller.TweakedLinkedControllerClientHandler;
import com.firepdx.ctc.gui.TweakedLinkedControllerScreen;
import com.firepdx.ctc.input.ModInputEvents;
import com.firepdx.ctc.item.ClientBindings;
import com.firepdx.ctc.item.ModItems;
import com.firepdx.ctc.item.TweakedLinkedControllerItemRenderer;
import com.firepdx.ctc.menu.ModMenuTypes;
import com.firepdx.ctc.ModBlockEntityTypes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererFactories;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.resources.ResourceLocation;

public class CreateTweakedControllersClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModClientConfig.load();
        ModKeyMappings.register();

        // Wire common-side hooks to their client-only implementations.
        ClientBindings.toggleActive = TweakedLinkedControllerClientHandler::toggle;
        ClientBindings.toggleBindMode = TweakedLinkedControllerClientHandler::toggleBindMode;
        ClientBindings.onLecternUserChanged = TweakedLinkedControllerClientHandler::onLecternUserChanged;

        ScreenRegistry.register(ModMenuTypes.TWEAKED_LINKED_CONTROLLER.get(), TweakedLinkedControllerScreen::new);
        BlockEntityRendererFactories.register(ModBlockEntityTypes.TWEAKED_LECTERN_CONTROLLER.get(), TweakedLecternControllerRenderer::new);

        // NOTE: assumes CustomRenderedItemModelRenderer (Create foundation class) inherits
        // BlockEntityWithoutLevelRenderer#renderByItem with the vanilla signature. Verify
        // against your Create-Fabric build if this fails to compile.
        TweakedLinkedControllerItemRenderer itemRenderer = new TweakedLinkedControllerItemRenderer();
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.TWEAKED_LINKED_CONTROLLER.get(), itemRenderer::renderByItem);

        HudElementRegistry.addLast(
            ResourceLocation.fromNamespaceAndPath("ctc", "linked_controller_overlay"),
            TweakedLinkedControllerClientHandler::renderOverlay);

        ClientTickEvents.END_CLIENT_TICK.register(client -> TweakedLinkedControllerClientHandler.tick());

        ModInputEvents.register();
    }
}
