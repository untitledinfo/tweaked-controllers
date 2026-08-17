package com.firepdx.ctc.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.firepdx.ctc.config.ModClientConfig;
import com.firepdx.ctc.item.ModItems;
import net.createmod.catnip.gui.ScreenOpener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

/**
 * The NeoForge ScreenEvent.Init.Post listener that used to place this button on the
 * title/pause screens is gone (no Fabric API equivalent for freely adding arbitrary
 * widgets to vanilla screens). It's replaced by two small mixins,
 * com.firepdx.ctc.mixin.client.TitleScreenMixin and PauseScreenMixin, which call
 * {@link #createButtonFor(Screen)} at the tail of each screen's init() and add the
 * result directly (mixins can call the protected Screen#addRenderableWidget; this class
 * can't, so it only builds the button — it never registers it).
 */
public class ModMainConfigButton extends Button
{
    public static final ItemStack ICON = ModItems.TWEAKED_LINKED_CONTROLLER.asStack(); // TODO maybe put an icon

    public ModMainConfigButton(int x, int y)
    {
        super(x, y, 20, 20, CommonComponents.EMPTY, ModMainConfigButton::click, DEFAULT_NARRATION);
    }

    @Override
    public void renderString(GuiGraphics graphics, Font pFont, int pColor)
    {
        // This prevents a crash with "remove loading screens", don't listen to the code cleanup recommendation
        Minecraft mc = Minecraft.getInstance();
        if (mc.getItemRenderer().getModel(ICON, null, null, 0) != null)
            graphics.renderItem(ICON, getX() + 2, getY() + 2);
    }

    public static void click(Button b)
    {
        ScreenOpener.open(new ModConfigScreen(Minecraft.getInstance().screen));
    }

    public static class SingleMenuRow
    {
        public final String left, right;
        public SingleMenuRow(String left, String right)
        {
            this.left = I18n.get(left);
            this.right = I18n.get(right);
        }
        public SingleMenuRow(String center) {
            this(center, center);
        }
    }

    public static class MenuRows
    {
        protected final List<String> leftButtons, rightButtons;

        public MenuRows(List<SingleMenuRow> variants)
        {
            leftButtons = variants.stream().map(r -> r.left).collect(Collectors.toList());
            rightButtons = variants.stream().map(r -> r.right).collect(Collectors.toList());
        }

        public static MenuRows CreateMainMenuRows()
        {
            return new MenuRows(Arrays.asList(
            new SingleMenuRow("menu.singleplayer"),
            new SingleMenuRow("menu.multiplayer"),
            new SingleMenuRow("fml.menu.mods", "menu.online"),
            new SingleMenuRow("narrator.button.language", "narrator.button.accessibility")
            ));
        }

        public static MenuRows CreateIngameMenuRows()
        {
            return new MenuRows(Arrays.asList(
            new SingleMenuRow("menu.returnToGame"),
            new SingleMenuRow("gui.advancements", "gui.stats"),
            new SingleMenuRow("menu.sendFeedback", "menu.reportBugs"),
            new SingleMenuRow("menu.options", "menu.shareToLan"),
            new SingleMenuRow("menu.returnToMenu")
        ));
        }
    }

    public static class OpenConfigButtonHandler
    {
        /** Builds (but does not register) the config button for the given screen, if applicable. */
        public static Optional<ModMainConfigButton> createButtonFor(Screen gui)
        {
            MenuRows menu = null;
            int rowIdx = 0, offsetX = 0;
            if (gui instanceof TitleScreen)
            {
                menu = MenuRows.CreateMainMenuRows();
                rowIdx = ModClientConfig.CONFIG_BUTTON_MAIN_MENU_ROW.get();
                offsetX = ModClientConfig.CONFIG_BUTTON_MAIN_MENU_OFFSET.get();
            }
            else if (gui instanceof PauseScreen)
            {
                menu = MenuRows.CreateIngameMenuRows();
                rowIdx = ModClientConfig.CONFIG_BUTTON_INGAME_MENU_ROW.get();
                offsetX = ModClientConfig.CONFIG_BUTTON_INGAME_MENU_OFFSET.get();
            }

            if (menu == null || rowIdx == 0)
                return Optional.empty();

            boolean onLeft = offsetX < 0;
            List<String> rowList = onLeft ? menu.leftButtons : menu.rightButtons;
            if (rowIdx - 1 < 0 || rowIdx - 1 >= rowList.size())
                return Optional.empty();
            String target = rowList.get(rowIdx - 1);

            for (var listener : gui.children())
            {
                if (!(listener instanceof AbstractWidget w))
                    continue;
                if (!w.getMessage().getString().equals(target))
                    continue;
                return Optional.of(new ModMainConfigButton(w.getX() + offsetX + (onLeft ? -20 : w.getWidth()), w.getY()));
            }
            return Optional.empty();
        }
    }
}
