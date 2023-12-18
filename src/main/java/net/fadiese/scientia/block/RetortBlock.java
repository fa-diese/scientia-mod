package net.fadiese.scientia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RetortBlock extends AbstractToolBlock {

    private final VoxelShape SHAPE_NORTH = Block.box(3, 0, 3, 13, 13, 12);
    private final VoxelShape SHAPE_SOUTH = Block.box(3, 0, 4, 13, 13, 13);
    private final VoxelShape SHAPE_EAST = Block.box(3, 0, 3, 12, 13, 13);
    private final VoxelShape SHAPE_WEST = Block.box(4, 0, 3, 13, 13, 13);

    public RetortBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (Direction.NORTH.equals(pState.getValue(FACING))) {
            return SHAPE_NORTH;
        } else if (Direction.SOUTH.equals(pState.getValue(FACING))) {
            return SHAPE_SOUTH;
        } else if (Direction.EAST.equals(pState.getValue(FACING))) {
            return SHAPE_EAST;
        } else {
            return SHAPE_WEST;
        }
    }
}
