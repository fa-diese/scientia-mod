package net.fadiese.scientia.block;

import net.fadiese.scientia.blockentity.ToolBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractToolBlock extends AbstractScientiaBlock {
    protected AbstractToolBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if (entity instanceof ToolBlockEntity) {
            ((ToolBlockEntity) entity).setDamage(pStack.getDamageValue());
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ToolBlockEntity(pPos, pState);
    }
}
