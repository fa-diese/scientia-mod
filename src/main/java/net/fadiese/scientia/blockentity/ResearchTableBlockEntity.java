package net.fadiese.scientia.blockentity;

import net.fadiese.scientia.data.ResearchFailure;
import net.fadiese.scientia.handler.ScientiaItemStackHandler;
import net.fadiese.scientia.inventory.ResearchTableMenu;
import net.fadiese.scientia.recipe.AdvancedResearchTableRecipe;
import net.fadiese.scientia.recipe.IResearchRecipe;
import net.fadiese.scientia.recipe.SimpleResearchTableRecipe;
import net.fadiese.scientia.recipe.ToolsAdditivesRecipe;
import net.fadiese.scientia.registry.ScientiaBlockEntities;
import net.fadiese.scientia.registry.ScientiaItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ResearchTableBlockEntity extends BlockEntity implements MenuProvider {

    private final ScientiaItemStackHandler itemHandler = new ScientiaItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            isRunning = 0;
            setChanged();
        }

        @Override
        public void onResultRetrieved(ServerPlayer player) {
            createExperience(player.getLevel(), player.position());
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

    protected final ContainerData data;

    private int hasIncompleteResearchNote = 0;
    private int hasRecipe = 0;
    private int isRunning = 0;
    private int maxProgress = 0;
    private int progress = 0;

    private int experience = 0;
    private ResearchFailure failure;

    public ResearchTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ScientiaBlockEntities.RESEARCH_TABLE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ResearchTableBlockEntity.this.hasIncompleteResearchNote;
                    case 1 -> ResearchTableBlockEntity.this.hasRecipe;
                    case 2 -> ResearchTableBlockEntity.this.isRunning;
                    case 3 -> ResearchTableBlockEntity.this.maxProgress;
                    case 4 -> ResearchTableBlockEntity.this.progress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ResearchTableBlockEntity.this.hasIncompleteResearchNote = pValue;
                    case 1 -> ResearchTableBlockEntity.this.hasRecipe = pValue;
                    case 2 -> ResearchTableBlockEntity.this.isRunning = pValue;
                    case 3 -> ResearchTableBlockEntity.this.maxProgress = pValue;
                    case 4 -> ResearchTableBlockEntity.this.progress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Research Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new ResearchTableMenu(pContainerId, pPlayerInventory, this, this.data);
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
        pTag.putInt("has_incomplete_rn", this.hasIncompleteResearchNote);
        pTag.putInt("has_recipe", this.hasRecipe);
        pTag.putInt("is_running", this.isRunning);
        pTag.putInt("max_progress", this.maxProgress);
        pTag.putInt("progress", this.progress);
        pTag.putInt("experience", this.experience);
        if (failure != null) {
            pTag.put("failure", ResearchFailure.CODEC.encodeStart(NbtOps.INSTANCE, failure).result().get());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        hasIncompleteResearchNote = pTag.getInt("has_incomplete_rn");
        hasRecipe = pTag.getInt("has_recipe");
        isRunning = pTag.getInt("is_running");
        progress = pTag.getInt("max_progress");
        maxProgress = pTag.getInt("progress");
        experience = pTag.getInt("experience");
        if (pTag.contains("failure")) {
            failure = ResearchFailure.CODEC.parse(NbtOps.INSTANCE, pTag.get("failure")).result().get();
        }
    }

    public void drops() {
        setInventory();
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void createExperience(ServerLevel pLevel, Vec3 pPopVec) {
        if (experience > 0) {
            ExperienceOrb.award(pLevel, pPopVec, experience);
            experience = 0;
        }
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, ResearchTableBlockEntity researchTableT) {
        boolean setChanged = false;
        Optional<? extends IResearchRecipe> recipe;
        researchTableT.setInventory();
        Optional<ToolsAdditivesRecipe> toolAdditives = level.getRecipeManager().getRecipeFor(ToolsAdditivesRecipe.Type.INSTANCE, researchTableT.inventory, level);
        if (researchTableT.inventory.getItem(4).is(ScientiaItems.RESEARCH_NOTES.get())) {
            if (researchTableT.hasIncompleteResearchNote == 0) {
                researchTableT.hasIncompleteResearchNote = 1;
                setChanged = true;
            }
            recipe = level.getRecipeManager().getRecipeFor(AdvancedResearchTableRecipe.Type.INSTANCE, researchTableT.inventory, level);
        } else {
            if (researchTableT.hasIncompleteResearchNote == 1) {
                researchTableT.hasIncompleteResearchNote = 0;
                setChanged = true;
            }
            recipe = level.getRecipeManager().getRecipeFor(SimpleResearchTableRecipe.Type.INSTANCE, researchTableT.inventory, level);
        }
        if (recipe.isPresent() && (toolAdditives.isEmpty() || toolAdditives.get().hasAdditive(researchTableT.inventory))) {
            if (researchTableT.isRunning == 1) {
                researchTableT.progress++;
                if (researchTableT.failure != null && researchTableT.progress >= researchTableT.failure.time()) {
                    researchTableT.triggerFailure(recipe.get(), toolAdditives.isPresent());
                } else if (researchTableT.progress >= researchTableT.maxProgress) {
                    researchTableT.craftResearchNote(recipe.get(), toolAdditives.isPresent());
                }
                setChanged = true;
            } else {
                if (researchTableT.progress != 0 || researchTableT.hasRecipe == 0 || researchTableT.maxProgress != recipe.get().duration()) {
                    setChanged = true;
                    researchTableT.progress = 0;
                    researchTableT.hasRecipe = 1;
                    researchTableT.maxProgress = recipe.get().duration();
                }
                ResearchFailure failure = recipe.get().getFailure(researchTableT.inventory);
                if (failure == null && researchTableT.failure != null) {
                    researchTableT.failure = null;
                    setChanged = true;
                } else if (failure != null && !failure.equals(researchTableT.failure)) {
                    researchTableT.failure = failure;
                    setChanged = true;
                }
            }
        } else {
            if (researchTableT.isRunning == 1 || researchTableT.progress != 0 || researchTableT.hasRecipe == 1 || researchTableT.maxProgress != 0) {
                setChanged = true;
                researchTableT.isRunning = 0;
                researchTableT.progress = 0;
                researchTableT.hasRecipe = 0;
                researchTableT.maxProgress = 0;
            }
        }
        if (setChanged) setChanged(level, blockPos, blockState);
    }

    private void craftResearchNote(IResearchRecipe recipe, boolean hasAdditive) {
        if (recipe.isAdvancedResearchRecipe()) {
            extractAdvancedSide(hasAdditive);
            itemHandler.setStackInSlot(7, recipe.assemble(inventory));
            hasIncompleteResearchNote = 0;
        } else {
            extractSimpleSide(hasAdditive);
            itemHandler.setStackInSlot(4, recipe.assemble(inventory));
            if (inventory.getItem(4).is(ScientiaItems.RESEARCH_NOTES.get())) hasIncompleteResearchNote = 1;
        }
        experience = recipe.experience();
        progress = 0;
        maxProgress = 0;
        hasRecipe = 0;
        isRunning = 0;
    }

    private void triggerFailure(IResearchRecipe recipe, boolean hasAdditive) {
        if (hasIncompleteResearchNote == 1) {
            extractAdvancedSide(hasAdditive);
            if (failure.generateNote()) {
                generateFailedNoteAdvancedSide(recipe, hasAdditive);
            }
            switch (failure.type()) {
                case explosion -> {
                    level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), failure.radius(), Explosion.BlockInteraction.DESTROY);
                }
                case item -> {
                    itemHandler.setStackInSlot(7, failure.getItemStackResult());
                }
                case potion_effect -> {
                    generateEffectCloud();
                }
            }
        } else {
            extractSimpleSide(hasAdditive);
            if (failure.generateNote()) {
                generateFailedNoteSimpleSide((SimpleResearchTableRecipe) recipe, hasAdditive);
            }
            switch (failure.type()) {
                case explosion -> {
                    level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), failure.radius(), Explosion.BlockInteraction.DESTROY);
                }
                case item -> {
                    itemHandler.setStackInSlot(4, failure.getItemStackResult());
                }
                case potion_effect -> {
                    generateEffectCloud();
                }
            }
        }
        failure = null;
        hasIncompleteResearchNote = 0;
        progress = 0;
        maxProgress = 0;
        hasRecipe = 0;
        isRunning = 0;
    }

    private void extractSimpleSide(boolean hasAdditive) {
        itemHandler.extractItem(0, 1, false);
        itemHandler.extractItem(1, 1, false);
        if (itemHandler.getStackInSlot(2).getItem().canBeDepleted() && itemHandler.getStackInSlot(2).hurt(10, level.getRandom(), null)) {
            itemHandler.extractItem(2, 1, false);
        }
        if (hasAdditive) {
            itemHandler.extractItem(3, 1, false);
        }
    }

    private void extractAdvancedSide(boolean hasAdditive) {
        itemHandler.extractItem(4, 1, false);
        if (itemHandler.getStackInSlot(5).getItem().canBeDepleted() && itemHandler.getStackInSlot(5).hurt(10, level.getRandom(), null)) {
            itemHandler.extractItem(5, 1, false);
        }
        if (hasAdditive) {
            itemHandler.extractItem(6, 1, false);
        }
    }

    private void generateFailedNoteSimpleSide(SimpleResearchTableRecipe recipe, boolean hasAdditive) {
        CompoundTag tags = new CompoundTag();
        tags.putString("scientia.topic", recipe.topic());
        tags.putInt("scientia.color", recipe.result().color().getValue());
        tags.putString("scientia.tool", Registry.ITEM.getKey(inventory.getItem(2).getItem()).toString());
        if (hasAdditive) {
            tags.putString("scientia.additive", Registry.ITEM.getKey(inventory.getItem(3).getItem()).toString());
        }
        ItemStack stack = new ItemStack(ScientiaItems.FAILED_EXPERIMENT_NOTE.get());
        stack.setTag(tags);
        itemHandler.setStackInSlot(7, stack);
    }

    private void generateFailedNoteAdvancedSide(IResearchRecipe recipe, boolean hasAdditive) {
        CompoundTag tags = new CompoundTag();
        tags.putString("scientia.topic", recipe.topic());
        tags.putInt("scientia.color", inventory.getItem(4).getTag().getInt("scientia.color"));
        tags.putString("scientia.tool", Registry.ITEM.getKey(inventory.getItem(5).getItem()).toString());
        if (hasAdditive) {
            tags.putString("scientia.additive", Registry.ITEM.getKey(inventory.getItem(6).getItem()).toString());
        }
        ItemStack stack = new ItemStack(ScientiaItems.FAILED_EXPERIMENT_NOTE.get());
        stack.setTag(tags);
        itemHandler.setStackInSlot(4, stack);
    }

    private void generateEffectCloud() {
        AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        areaeffectcloud.setRadius(failure.radius());
        areaeffectcloud.setRadiusOnUse(-0.5F);
        areaeffectcloud.setWaitTime(10);
        areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());
        areaeffectcloud.setPotion(new Potion(new MobEffectInstance(failure.effect(), 100, failure.amplifier())));
        level.addFreshEntity(areaeffectcloud);
    }

    private void setInventory() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
    }
}
