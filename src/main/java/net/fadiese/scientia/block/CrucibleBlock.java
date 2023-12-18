package net.fadiese.scientia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CrucibleBlock extends AbstractToolBlock {

    private final VoxelShape SHAPE_NORTH_SOUTH = Block.box(1, 0, 4, 15, 5, 12);
    private final VoxelShape SHAPE_EAST_WEST = Block.box(4, 0, 1, 12, 5, 15);

    public CrucibleBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (Direction.NORTH.equals(pState.getValue(FACING)) || Direction.SOUTH.equals(pState.getValue(FACING))) {
            return SHAPE_NORTH_SOUTH;
        } else {
            return SHAPE_EAST_WEST;
        }
    }
}
