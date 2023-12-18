package net.fadiese.scientia.item;

import net.fadiese.scientia.client.tooltip.TopicTooltip;
import net.fadiese.scientia.helper.TopicHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FailedExperimentNoteItem extends Item {

    public FailedExperimentNoteItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!stack.hasTag()) {
            components.add(Component.translatable("gui.researchNote.noTopic").withStyle(ChatFormatting.GRAY));
        } else {
            StringBuilder str = new StringBuilder(Registry.ITEM.get(ResourceLocation.tryParse(stack.getTag().getString("scientia.tool"))).getDescription().getString());
            if (stack.getTag().contains("scientia.additive")) {
                str.append(" - ");
                str.append(Registry.ITEM.get(ResourceLocation.tryParse(stack.getTag().getString("scientia.additive"))).getDescription().getString());
            }
            components.add(Component.literal(str.toString()).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        if (pStack.hasTag()) {
            return Optional.of(new TopicTooltip(TopicHelper.getInstance().getTopicComponent(pStack.getTag().getString("scientia.topic"))));
        } else {
            return Optional.empty();
        }
    }
}
