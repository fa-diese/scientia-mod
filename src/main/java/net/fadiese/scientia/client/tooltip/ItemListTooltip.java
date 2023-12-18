package net.fadiese.scientia.client.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemListTooltip(List<ItemStack> items) implements TooltipComponent {
}
