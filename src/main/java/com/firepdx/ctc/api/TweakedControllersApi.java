package com.firepdx.ctc.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Public API surface for Tweaked Controllers.
 * <p>
 * Other mods should depend on this module only (not the full mod jar internals)
 * to add or react to controller behavior. Grab the singleton via {@link #INSTANCE}.
 * <p>
 * Example (from another mod):
 * <pre>{@code
 * TweakedControllersApi api = TweakedControllersApi.INSTANCE;
 * api.registerBehavior(MyMod.MY_CONTROLLER, (world, pos, powered) -> {
 *     // react to toggle
 * });
 * Item item = api.getItemFromBlock(MyMod.MY_CONTROLLER);
 * }</pre>
 */
public final class TweakedControllersApi {

    /** Global singleton — the API has no per-world state, so one instance is shared. */
    public static final TweakedControllersApi INSTANCE = new TweakedControllersApi();

    private final Map<Block, ControllerBehavior> behaviors = new LinkedHashMap<>();
    private final CopyOnWriteArrayList<ControllerToggleListener> globalListeners = new CopyOnWriteArrayList<>();

    private TweakedControllersApi() {
    }

    // ---------------------------------------------------------------
    // Block <-> Item helpers
    // ---------------------------------------------------------------

    /**
     * Resolves the {@link Item} form of a given {@link Block}, e.g. to build an
     * {@link ItemStack} for drops, JEI/REI display, or GUI rendering.
     * <p>
     * Equivalent to {@code Item.BLOCK_ITEMS.get(block)} but safe against
     * blocks that have no associated item (air, fluids, etc.) — returns
     * {@link Item#AIR} in that case instead of null.
     *
     * @param block the block to resolve
     * @return the item that places this block, or {@link Item#AIR} if none exists
     */
    public Item getItemFromBlock(Block block) {
        if (block == null) {
            return net.minecraft.item.Items.AIR;
        }
        Item item = block.asItem();
        return item != null ? item : net.minecraft.item.Items.AIR;
    }

    /**
     * Same as {@link #getItemFromBlock(Block)} but returns an {@link Optional},
     * useful when callers want to distinguish "no item" from "air block".
     */
    public Optional<Item> getItemFromBlockOptional(Block block) {
        if (block == null) {
            return Optional.empty();
        }
        Item item = block.asItem();
        return item == net.minecraft.item.Items.AIR ? Optional.empty() : Optional.ofNullable(item);
    }

    /**
     * Convenience overload that also builds a single-count {@link ItemStack}.
     */
    public ItemStack getStackFromBlock(Block block) {
        return new ItemStack(getItemFromBlock(block));
    }

    /**
     * Looks a block up by its registry id string, e.g. {@code "ctc:controller_block"}
     * or a foreign mod's id such as {@code "create:mechanical_press"}, then resolves
     * its item form. Returns {@link Item#AIR} if the id is unknown.
     */
    public Item getItemFromBlockId(String identifier) {
        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(identifier);
        if (id == null || !Registries.BLOCK.containsId(id)) {
            return net.minecraft.item.Items.AIR;
        }
        Block block = Registries.BLOCK.get(id);
        return getItemFromBlock(block);
    }

    // ---------------------------------------------------------------
    // Mod-support: behavior registration for third-party controller blocks
    // ---------------------------------------------------------------

    /**
     * Registers custom behavior for a controller block so other mods don't need
     * to subclass {@link com.firepdx.ctc.block.ControllerBlock} directly.
     *
     * @param block    the block instance (yours or ours) that should trigger this behavior
     * @param behavior callback invoked whenever the block's powered state changes
     */
    public void registerBehavior(Block block, ControllerBehavior behavior) {
        behaviors.put(block, behavior);
    }

    /** Removes a previously registered behavior, if present. */
    public void unregisterBehavior(Block block) {
        behaviors.remove(block);
    }

    /** Subscribes to every controller toggle in the game, regardless of block type. */
    public void addGlobalToggleListener(ControllerToggleListener listener) {
        globalListeners.add(listener);
    }

    public void removeGlobalToggleListener(ControllerToggleListener listener) {
        globalListeners.remove(listener);
    }

    /**
     * Internal dispatcher — called by controller blocks when toggled.
     * Not typically called by other mods directly.
     */
    public void dispatchToggle(World world, BlockPos pos, boolean powered) {
        Block block = world.getBlockState(pos).getBlock();
        ControllerBehavior behavior = behaviors.get(block);
        if (behavior != null) {
            behavior.onToggle(world, pos, powered);
        }
        for (ControllerToggleListener listener : globalListeners) {
            listener.onToggle(world, pos, block, powered);
        }
    }

    // ---------------------------------------------------------------
    // Functional interfaces
    // ---------------------------------------------------------------

    @FunctionalInterface
    public interface ControllerBehavior {
        void onToggle(World world, BlockPos pos, boolean powered);
    }

    @FunctionalInterface
    public interface ControllerToggleListener {
        void onToggle(World world, BlockPos pos, Block block, boolean powered);
    }
}
