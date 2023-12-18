package net.fadiese.scientia.item;

import net.fadiese.scientia.client.tooltip.TopicTooltip;
import net.fadiese.scientia.helper.TopicHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ResearchNotesItem extends Item {

    public ResearchNotesItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!stack.hasTag()) {
            components.add(Component.translatable("gui.researchNote.noTopic").withStyle(ChatFormatting.GRAY));
        } else {
            if (stack.getTag().getBoolean("scientia.completed")) {
                components.add(Component.translatable("gui.researchNote.complete").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            } else {
                components.add(Component.translatable("gui.researchNote.incomplete").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
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
