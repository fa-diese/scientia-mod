package net.fadiese.scientia.inventory;

import net.fadiese.scientia.registry.ScientiaItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FailureNoteSlot extends SlotItemHandler {

    public FailureNoteSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(ScientiaItems.FAILED_EXPERIMENT_NOTE.get());
    }
}
