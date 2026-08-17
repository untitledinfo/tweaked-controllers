package com.firepdx.ctc.block;

import com.firepdx.ctc.CreateTweakedControllers;
import com.firepdx.ctc.util.RegistryEntries.BlockEntry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks
{
    public static final BlockEntry<TweakedLecternControllerBlock> TWEAKED_LECTERN_CONTROLLER = register(
        "tweaked_lectern_controller",
        TweakedLecternControllerBlock::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)
    );

    private static <T extends net.minecraft.world.level.block.Block> BlockEntry<T> register(
        String path, java.util.function.Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties)
    {
        ResourceLocation id = CreateTweakedControllers.asResource(path);
        // NOTE: MC 1.21.2+ requires BlockBehaviour.Properties#setId(ResourceKey) before
        // construction. 1.21.1 does not — if you bump the target version, add that call here.
        T block = factory.apply(properties);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        return new BlockEntry<>(block);
    }

    public static void register() {}
}
