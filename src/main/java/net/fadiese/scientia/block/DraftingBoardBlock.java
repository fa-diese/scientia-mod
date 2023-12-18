package net.fadiese.scientia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DraftingBoardBlock extends AbstractToolBlock {

    private final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0, 0, 0, 16, 4, 4), Block.box(0, 0, 4, 16, 7, 7), Block.box(0, 0, 7, 16, 10, 10));
    private final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0, 0, 6, 16, 10, 9), Block.box(0, 0, 9, 16, 7, 12), Block.box(0, 0, 12, 16, 4, 16));
    private final VoxelShape SHAPE_EAST = Shapes.or(Block.box(6, 0, 0, 9, 10, 16), Block.box(9, 0, 0, 12, 7, 16), Block.box(12, 0, 0, 16, 4, 16));
    private final VoxelShape SHAPE_WEST = Shapes.or(Block.box(0, 0, 0, 4, 4, 16), Block.box(4, 0, 0, 7, 7, 16), Block.box(7, 0, 0, 10, 10, 16));

    public DraftingBoardBlock(Properties pProperties) {
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
