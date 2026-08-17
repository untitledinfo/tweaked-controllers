package com.firepdx.ctc.gui;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.config.ModClientConfig;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

/**
 * Was: routes the "general config" button through Create/Catnip's SubMenuConfigScreen,
 * built against NeoForge's ModConfigSpec. Since this port dropped ModConfigSpec for a
 * plain JSON config (see ModClientConfig), that button now opens a small in-house
 * ModGeneralConfigScreen instead. If your Create-Fabric build's Catnip has its own
 * config-screen abstraction you'd rather use, swap this back in.
 */
public class ModConfigScreen extends AbstractSimiScreen
{
    protected final Screen parent;
    protected boolean returnOnClose;
    protected Button advancedConfigButton;

    public ModConfigScreen(Screen parent)
    {
        this.parent = parent;
        this.returnOnClose = true;
    }

    @Override
    protected void init()
    {
        super.init();
        this.returnOnClose = true;
        this.Populate();
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (ModClientConfig.USE_CUSTOM_MAPPINGS.get())
        {
            advancedConfigButton.active = true;
            advancedConfigButton.setTooltip(null);
        }
        else
        {
            advancedConfigButton.active = false;
            advancedConfigButton.setTooltip(Tooltip.create(CreateTweakedControllers.translateDirect("menu.config_disabled").withStyle(s -> s.withColor(0xFC785C).withBold(true))));
        }
    }

    private void Populate()
    {
        int yStart = height / 4 + 40;
        int center = width / 2;
        int bHeight = 20;
        int bShortWidth = 98;
        int bLongWidth = 200;
        addRenderableWidget(Button.builder(CreateTweakedControllers.translateDirect("menu.return"), ($) -> {
            this.linkTo(parent);
        }).bounds(center - 100, yStart + 92, bLongWidth, bHeight).build());
        addRenderableWidget(Button.builder(CreateTweakedControllers.translateDirect("menu.config_general"), ($) -> {
            this.linkTo(new ModGeneralConfigScreen(this));
        }).bounds(center - 100, yStart + 8, bLongWidth, bHeight).build());
        advancedConfigButton = new Button.Builder(CreateTweakedControllers.translateDirect("menu.config_controller"), ($) -> {
            this.linkTo((new ModControllerConfigScreen(this)));
        }).bounds(center - 100, yStart + 32, bLongWidth, bHeight).build();
        addRenderableWidget(advancedConfigButton);
        addRenderableWidget(Button.builder(CreateTweakedControllers.translateDirect("menu.issues"), ($) -> {
            this.linkTo("https://github.com/getItemFromBlock/Create-Tweaked-Controllers/issues");
        }).bounds(center + 2, yStart + 68, bShortWidth, bHeight).build());
        addRenderableWidget(Button.builder(CreateTweakedControllers.translateDirect("menu.wiki"), ($) -> {
            this.linkTo("https://github.com/getItemFromBlock/Create-Tweaked-Controllers/wiki");
        }).bounds(center - 100, yStart + 68, bShortWidth, bHeight).build());
    }

    private void linkTo(Screen screen)
    {
        returnOnClose = false;
        ScreenOpener.open(screen);
    }

   private void linkTo(String url)
   {
        returnOnClose = false;
        ScreenOpener.open(new ConfirmLinkScreen((p) -> {
            if (p)
            {
                Util.getPlatform().openUri(url);
            }
            this.minecraft.setScreen(this);
        }, url, true));
    }

    public boolean isPauseScreen()
    {
        return true;
    }

}
