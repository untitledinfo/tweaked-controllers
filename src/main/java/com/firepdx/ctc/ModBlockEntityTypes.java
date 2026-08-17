package com.firepdx.ctc;

import com.firepdx.ctc.block.ModBlocks;
import com.firepdx.ctc.block.TweakedLecternControllerBlockEntity;
import com.firepdx.ctc.util.RegistryEntries.BlockEntityEntry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * The renderer ({@code TweakedLecternControllerRenderer}) is registered client-side
 * in {@code com.firepdx.ctc.client.CreateTweakedControllersClient} via
 * {@code BlockEntityRendererFactories.register}, since renderer registration doesn't
 * exist on the common block entity type on Fabric.
 */
public class ModBlockEntityTypes
{
    public static final BlockEntityEntry<BlockEntityType<TweakedLecternControllerBlockEntity>> TWEAKED_LECTERN_CONTROLLER =
        register("tweaked_lectern_controller", TweakedLecternControllerBlockEntity::new);

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityEntry<BlockEntityType<T>> register(
        String path, net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory<T> factory)
    {
        ResourceLocation id = CreateTweakedControllers.asResource(path);
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory, ModBlocks.TWEAKED_LECTERN_CONTROLLER.get()).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type);
        return new BlockEntityEntry<>(type);
    }

    public static void register() {}
}
