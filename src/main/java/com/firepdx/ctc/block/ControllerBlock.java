package com.firepdx.ctc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Base "controller" block. Other mods can extend this or implement
 * {@link com.firepdx.ctc.api.ControllerBehavior} and register it through
 * {@link com.firepdx.ctc.api.TweakedControllersApi} instead of subclassing,
 * which is the preferred way to add third-party controller support.
 */
public class ControllerBlock extends Block implements Waterloggable {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);

    public ControllerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(POWERED, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, net.minecraft.entity.player.PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            boolean newState = !state.get(POWERED);
            world.setBlockState(pos, state.with(POWERED, newState), Block.NOTIFY_ALL);
            com.firepdx.ctc.api.TweakedControllersApi.INSTANCE.dispatchToggle(world, pos, newState);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public net.minecraft.fluid.FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        boolean waterlogged = ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER;
        return getDefaultState().with(WATERLOGGED, waterlogged);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POWERED) ? 15 : 0;
    }
}
