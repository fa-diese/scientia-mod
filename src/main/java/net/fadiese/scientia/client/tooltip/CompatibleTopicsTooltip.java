package net.fadiese.scientia.client.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

public record CompatibleTopicsTooltip(List<Component> compatibleTopics) implements TooltipComponent {
}
