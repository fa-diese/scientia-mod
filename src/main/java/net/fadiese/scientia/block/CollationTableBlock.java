package net.fadiese.scientia.block;

import net.fadiese.scientia.blockentity.CollationTableBlockEntity;
import net.fadiese.scientia.registry.ScientiaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CollationTableBlock extends AbstractScientiaBlock {

    private final VoxelShape BASE = Block.box(0, 13, 0, 16, 16, 16);

    private final VoxelShape LEG_NORTH_EAST = Block.box(13, 0, 1, 15, 13, 3);

    private final VoxelShape LEG_SOUTH_EAST = Block.box(13, 0, 13, 15, 13, 15);

    private final VoxelShape LEG_SOUTH_WEST = Block.box(1, 0, 13, 3, 13, 15);

    private final VoxelShape LEG_NORTH_WEST = Block.box(1, 0, 1, 3, 13, 3);

    private final VoxelShape SHAPE_NORTH = Shapes.or(BASE, Block.box(1, 0, 1, 6, 13, 15), LEG_NORTH_EAST, LEG_SOUTH_EAST);
    private final VoxelShape SHAPE_SOUTH = Shapes.or(BASE, Block.box(10, 0, 1, 15, 13, 15), LEG_NORTH_WEST, LEG_SOUTH_WEST);
    private final VoxelShape SHAPE_EAST = Shapes.or(BASE, Block.box(1, 0, 1, 15, 13, 6), LEG_SOUTH_EAST, LEG_SOUTH_WEST);
    private final VoxelShape SHAPE_WEST = Shapes.or(BASE, Block.box(1, 0, 10, 15, 13, 15), LEG_NORTH_EAST, LEG_NORTH_WEST);

    public CollationTableBlock(Properties pProperties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new CollationTableBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ScientiaBlockEntities.COLLATION_TABLE.get(), CollationTableBlockEntity::serverTick);
    }

    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CollationTableBlockEntity) {
                ((CollationTableBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof CollationTableBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, (CollationTableBlockEntity) entity, pPos);
            } else {
                throw new IllegalStateException("Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
}
