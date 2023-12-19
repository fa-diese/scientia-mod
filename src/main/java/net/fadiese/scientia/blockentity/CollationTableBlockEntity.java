package net.fadiese.scientia.blockentity;

import net.fadiese.scientia.inventory.CollationTableMenu;
import net.fadiese.scientia.recipe.CollationTableRecipe;
import net.fadiese.scientia.registry.ScientiaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CollationTableBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            isRunning = 0;
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int hasRecipe = 0;
    private int isRunning = 0;
    private int requiredKnowledge = 0;
    private int levelCost = 0;
    private int chanceCategory = 0;
    private int progress = 0;
    private int maxProgress = 0;

    public SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

    public CollationTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ScientiaBlockEntities.COLLATION_TABLE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CollationTableBlockEntity.this.hasRecipe;
                    case 1 -> CollationTableBlockEntity.this.isRunning;
                    case 2 -> CollationTableBlockEntity.this.requiredKnowledge;
                    case 3 -> CollationTableBlockEntity.this.levelCost;
                    case 4 -> CollationTableBlockEntity.this.chanceCategory;
                    case 5 -> CollationTableBlockEntity.this.progress;
                    case 6 -> CollationTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CollationTableBlockEntity.this.hasRecipe = pValue;
                    case 1 -> CollationTableBlockEntity.this.isRunning = pValue;
                    case 2 -> CollationTableBlockEntity.this.requiredKnowledge = pValue;
                    case 3 -> CollationTableBlockEntity.this.levelCost = pValue;
                    case 4 -> CollationTableBlockEntity.this.chanceCategory = pValue;
                    case 5 -> CollationTableBlockEntity.this.progress = pValue;
                    case 6 -> CollationTableBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 7;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Collation Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CollationTableMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side == null) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("progress", this.progress);
        pTag.putInt("max_progress", this.maxProgress);
        pTag.putInt("has_recipe", this.hasRecipe);
        pTag.putInt("is_running", this.isRunning);
        pTag.putInt("required_knowledge", this.requiredKnowledge);
        pTag.putInt("level_cost", this.levelCost);
        pTag.putInt("chance_category", this.chanceCategory);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        maxProgress = pTag.getInt("max_progress");
        progress = pTag.getInt("progress");
        hasRecipe = pTag.getInt("has_recipe");
        isRunning = pTag.getInt("is_running");
        requiredKnowledge = pTag.getInt("required_knowledge");
        levelCost = pTag.getInt("level_cost");
        chanceCategory = pTag.getInt("chance_category");
    }

    public void drops() {
        setInventory();
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, CollationTableBlockEntity collationTableT) {
        boolean setChanged = false;
        collationTableT.setInventory();
        Optional<CollationTableRecipe> recipe = level.getRecipeManager().getRecipeFor(CollationTableRecipe.Type.INSTANCE, collationTableT.inventory, level);
        if (recipe.isPresent()) {
            if (collationTableT.isRunning == 1) {
                collationTableT.progress++;
                if (collationTableT.progress >= collationTableT.maxProgress) {
                    collationTableT.craftItem(recipe.get());
                }
                setChanged = true;
            } else {
                double threshold = recipe.get().getThresholdChances(collationTableT.inventory);
                int calculatedChance = threshold < 1 ? (int) Math.ceil(threshold * 5) : 5;
                if (collationTableT.hasRecipe != 1 || collationTableT.chanceCategory != calculatedChance || collationTableT.requiredKnowledge != recipe.get().knowledgeLevel() || collationTableT.levelCost != recipe.get().levelCost() || collationTableT.progress != 0 || collationTableT.maxProgress != recipe.get().duration()) {
                    setChanged = true;
                    collationTableT.hasRecipe = 1;
                    collationTableT.chanceCategory = calculatedChance;
                    collationTableT.requiredKnowledge = recipe.get().knowledgeLevel();
                    collationTableT.levelCost = recipe.get().levelCost();
                    collationTableT.maxProgress = recipe.get().duration();
                    collationTableT.progress = 0;
                }
            }
        } else {
            if (collationTableT.isRunning != 0 || collationTableT.hasRecipe != 0 || collationTableT.chanceCategory != 0 || collationTableT.requiredKnowledge != 0 || collationTableT.levelCost != 0 || collationTableT.progress != 0 || collationTableT.maxProgress != 0) {
                setChanged = true;
                collationTableT.isRunning = 0;
                collationTableT.hasRecipe = 0;
                collationTableT.chanceCategory = 0;
                collationTableT.requiredKnowledge = 0;
                collationTableT.levelCost = 0;
                collationTableT.maxProgress = 0;
                collationTableT.progress = 0;
            }
        }
        if (setChanged) setChanged(level, blockPos, blockState);
    }

    private void craftItem(CollationTableRecipe recipe) {
        itemHandler.extractItem(0, 1, false);
        itemHandler.extractItem(1, 1, false);
        itemHandler.extractItem(2, 1, false);
        itemHandler.setStackInSlot(6, recipe.assemble(inventory));
        isRunning = 0;
        progress = 0;
        maxProgress = 0;
        hasRecipe = 0;
        requiredKnowledge = 0;
        levelCost = 0;
        chanceCategory = 0;
    }

    private void setInventory() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
    }
}
