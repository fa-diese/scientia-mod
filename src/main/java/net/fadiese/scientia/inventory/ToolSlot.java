package net.fadiese.scientia.inventory;

import net.fadiese.scientia.Scientia;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ToolSlot extends SlotItemHandler {
    public ToolSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(Scientia.RESEARCH_TOOLS);
    }
}
