package com.firepdx.ctc.mixin.client;

import com.firepdx.ctc.gui.ModMainConfigButton;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen
{
    protected PauseScreenMixin(net.minecraft.network.chat.Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void ctc$addConfigButton(CallbackInfo ci)
    {
        ModMainConfigButton.OpenConfigButtonHandler.createButtonFor(this).ifPresent(this::addRenderableWidget);
    }
}
