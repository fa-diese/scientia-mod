package net.fadiese.scientia.blockentity;

import net.fadiese.scientia.registry.ScientiaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ToolBlockEntity extends BlockEntity {
    private int damage = 0;

    public ToolBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ScientiaBlockEntities.TOOL_ENTITY.get(), pPos, pBlockState);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        damage = nbt.getInt("Damage");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Damage", damage);
    }
}
