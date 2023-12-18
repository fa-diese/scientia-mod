package net.fadiese.scientia.inventory;

import net.fadiese.scientia.blockentity.CollationTableBlockEntity;
import net.fadiese.scientia.registry.ScientiaBlocks;
import net.fadiese.scientia.registry.ScientiaItems;
import net.fadiese.scientia.registry.ScientiaMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CollationTableMenu extends AbstractContainerMenu {

    public final CollationTableBlockEntity entity;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private int knowledgeLevel;

    public CollationTableMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, Objects.requireNonNull(inv.player.level.getBlockEntity(extraData.readBlockPos())), new SimpleContainerData(7));
    }

    public CollationTableMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ScientiaMenuTypes.COLLATION_TABLE_CONTAINER.get(), id);
        this.entity = (CollationTableBlockEntity) entity;
        this.access = ContainerLevelAccess.create(inv.player.level, entity.getBlockPos());
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new ResearchNoteSlot(handler, 0, 17, 28));
            this.addSlot(new ResearchNoteSlot(handler, 1, 17, 46));
            this.addSlot(new BookSlot(handler, 2, 17, 73));
            this.addSlot(new FailureNoteSlot(handler, 3, 51, 58));
            this.addSlot(new FailureNoteSlot(handler, 4, 69, 58));
            this.addSlot(new FailureNoteSlot(handler, 5, 87, 58));
            this.addSlot(new ResearchResultSlot(handler, 6, 125, 74, inv.player));
        });
        addDataSlots(data);
        calculateKnowledgeLevel();
    }

    public int getProgress() {
        return data.get(5);
    }

    public int getScaledProgress() {
        int progress = data.get(5);
        int maxProgress = data.get(6);
        int progressArrowSize = 72;
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public boolean isRunning() {
        return data.get(1) == 1;
    }

    public boolean hasRecipe() {
        return data.get(0) == 1;
    }

    public boolean hasRequiredKnowledgeLevel() {
        return knowledgeLevel >= data.get(2);
    }

    public int getRequiredKnowledgeLevel() {
        return data.get(2);
    }

    public int getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public int getLevelCost() {
        return data.get(3);
    }

    public int getChanceCategory() {
        return data.get(4);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 7;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, ScientiaBlocks.COLLATION_TABLE.get());
    }

    public String getTopicInSlot(int index) {
        Slot slot = slots.get(index);
        if (slot.getItem().is(ScientiaItems.RESEARCH_NOTES.get()) && slot.getItem().hasTag()) {
            return slot.getItem().getTag().getString("scientia.topic");
        }
        return "";
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 104 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 162));
        }
    }

    public boolean clickMenuButton(@NotNull Player pPlayer, int pId) {
        if (pId == 2) {
            calculateKnowledgeLevel();
            if (hasRequiredKnowledgeLevel() && (pPlayer.experienceLevel >= data.get(3) || pPlayer.getAbilities().instabuild)) {
                pPlayer.giveExperienceLevels(-(data.get(3)));
                this.data.set(1, 1);
                return true;
            }
            return false;
        } else {
            this.data.set(1, 0);
            return true;
        }
    }

    private void calculateKnowledgeLevel() {
        this.access.execute((level, blockPos) -> {
            float j = 0;
            for (BlockPos blockpos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
                if (EnchantmentTableBlock.isValidBookShelf(level, blockPos, blockpos)) {
                    j += level.getBlockState(blockPos.offset(blockpos)).getEnchantPowerBonus(level, blockPos.offset(blockpos));
                }
            }
            knowledgeLevel = Math.round(j);
        });
        this.broadcastChanges();
    }
}
