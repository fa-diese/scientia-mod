package net.fadiese.scientia.inventory;

import net.fadiese.scientia.blockentity.ResearchTableBlockEntity;
import net.fadiese.scientia.registry.ScientiaBlocks;
import net.fadiese.scientia.registry.ScientiaMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class ResearchTableMenu extends AbstractContainerMenu {
    public final ResearchTableBlockEntity entity;
    private final Level level;
    private final ContainerData data;

    public ResearchTableMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    public ResearchTableMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ScientiaMenuTypes.RESEARCH_TABLE_CONTAINER.get(), id);
        checkContainerSize(inv, 8);
        this.entity = (ResearchTableBlockEntity) entity;
        this.level = inv.player.level;
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 44, 23));
            this.addSlot(new PaperSlot(handler, 1, 44, 41));
            this.addSlot(new ToolSlot(handler, 2, 44, 62));
            this.addSlot(new SlotItemHandler(handler, 3, 44, 80));
            this.addSlot(new ResearchResultSlot(handler, 4, 98, 41, inv.player));
            this.addSlot(new ToolSlot(handler, 5, 98, 62));
            this.addSlot(new SlotItemHandler(handler, 6, 98, 80));
            this.addSlot(new ResearchResultSlot(handler, 7, 134, 62, inv.player));
        });
        addDataSlots(data);
    }


    public boolean hasIncompleteResearchNote() {
        return this.data.get(0) == 1;
    }

    public boolean hasRecipe() {
        return this.data.get(1) == 1;
    }

    public boolean isRunning() {
        return this.data.get(2) == 1;
    }

    public int getMaxProgress() {
        return this.data.get(3);
    }

    public int getProgress() {
        return this.data.get(4);
    }

    public boolean hasTool() {
        return (hasIncompleteResearchNote() && slots.get(41).hasItem()) || (!hasIncompleteResearchNote() && slots.get(38).hasItem());
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 8;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
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
        return stillValid(ContainerLevelAccess.create(level, entity.getBlockPos()), pPlayer, ScientiaBlocks.RESEARCH_TABLE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 122 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 180));
        }
    }


    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (pId == 2) {
            this.data.set(2, 1);
            return true;
        }
        this.data.set(2, 0);
        return true;
    }
}
