package com.firepdx.ctc.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.inventory.MenuType;

/**
 * The original mod used Create's Registrate ({@code BlockEntry}, {@code ItemEntry},
 * {@code BlockEntityEntry}, {@code MenuEntry}) throughout its call sites (e.g.
 * {@code ModBlocks.FOO.get()}, {@code ModBlocks.FOO.has(state)}, {@code ModItems.FOO.asStack()}).
 * <p>
 * Rather than touch every call site, these small wrappers reproduce that same surface
 * while registering directly against plain Fabric/vanilla registries — no Registrate
 * dependency required. If your Create-Fabric build does ship Registrate, you can drop
 * these and go back to the original {@code CreateRegistrate} calls instead.
 */
public final class RegistryEntries
{
    private RegistryEntries() {}

    public static final class BlockEntry<T extends Block>
    {
        private final T value;
        public BlockEntry(T value) { this.value = value; }
        public T get() { return value; }
        public boolean has(BlockState state) { return state.is(value); }
        public ItemStack asStack() { return new ItemStack(value); }
    }

    public static final class ItemEntry<T extends Item> implements ItemLike
    {
        private final T value;
        public ItemEntry(T value) { this.value = value; }
        public T get() { return value; }
        public ItemStack asStack() { return new ItemStack(value); }
        public boolean isIn(ItemStack stack) { return stack.is(value); }
        @Override public Item asItem() { return value; }
    }

    public static final class BlockEntityEntry<T extends BlockEntityType<?>>
    {
        private final T value;
        public BlockEntityEntry(T value) { this.value = value; }
        public T get() { return value; }
    }

    public static final class MenuTypeEntry<T extends MenuType<?>>
    {
        private final T value;
        public MenuTypeEntry(T value) { this.value = value; }
        public T get() { return value; }
    }
}
