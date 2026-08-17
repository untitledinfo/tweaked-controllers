package com.firepdx.ctc.mixin.client;

import com.firepdx.ctc.gui.ModMainConfigButton;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen
{
    protected TitleScreenMixin(net.minecraft.network.chat.Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void ctc$addConfigButton(CallbackInfo ci)
    {
        ModMainConfigButton.OpenConfigButtonHandler.createButtonFor(this).ifPresent(this::addRenderableWidget);
    }
}
