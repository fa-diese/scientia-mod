package net.fadiese.scientia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.data.ResearchFailure;
import net.fadiese.scientia.registry.ScientiaItems;
import net.fadiese.scientia.registry.ScientiaRecipes;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public record AdvancedResearchTableRecipe(ResourceLocation id, String topic, Map<Item, List<Item>> properToolsAdditives,
                                          int duration, Map<Item, Map<String, ResearchFailure>> failures,
                                          int experience) implements IResearchRecipe {

    public static Codec<AdvancedResearchTableRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("topic").forGetter(AdvancedResearchTableRecipe::topic),
                Codec.unboundedMap(Registry.ITEM.byNameCodec(), Registry.ITEM.byNameCodec().listOf()).fieldOf("properToolsAdditives").forGetter(AdvancedResearchTableRecipe::properToolsAdditives),
                Codec.INT.fieldOf("duration").orElse(1200).forGetter(AdvancedResearchTableRecipe::duration),
                Codec.unboundedMap(Registry.ITEM.byNameCodec(), Codec.unboundedMap(Codec.STRING, ResearchFailure.CODEC)).fieldOf("failures").forGetter(AdvancedResearchTableRecipe::failures),
                Codec.INT.fieldOf("experience").orElse(0).forGetter(AdvancedResearchTableRecipe::experience)
        ).apply(instance, AdvancedResearchTableRecipe::new));
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        ItemStack incompleteResearchNoteItem = pContainer.getItem(4);
        return !pLevel.isClientSide() && incompleteResearchNoteItem.is(ScientiaItems.RESEARCH_NOTES.get())
                && incompleteResearchNoteItem.hasTag() && topic.equals(incompleteResearchNoteItem.getTag().getString("scientia.topic"))
                && pContainer.getItem(5).is(Scientia.RESEARCH_TOOLS) && pContainer.getItem(7).isEmpty();
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        if (!hasProperToolsAdditives(pContainer)) {
            return new ItemStack(ScientiaItems.CRUMPLED_PAPER_BALL.get(), 1);
        }
        ItemStack stack = getResultItem();
        stack.getTag().putInt("scientia.color", pContainer.getItem(4).getTag().getInt("scientia.color"));
        return stack;
    }

    private boolean hasProperToolsAdditives(SimpleContainer pContainer) {
        List<Item> properAdditives = properToolsAdditives.get(pContainer.getItem(5).getItem());
        return properAdditives != null && (properAdditives.isEmpty() || properAdditives.contains(pContainer.getItem(6).getItem()));
    }

    public ResearchFailure getFailure(SimpleContainer pContainer) {
        if (!hasProperToolsAdditives(pContainer)) {
            Map<String, ResearchFailure> failuresForTool = failures.get(pContainer.getItem(5).getItem());
            if (failuresForTool != null && !failuresForTool.isEmpty()) {
                return failuresForTool.getOrDefault(Registry.ITEM.getKey(pContainer.getItem(6).getItem()).toString(), failuresForTool.get("default"));
            }
        }
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        CompoundTag topicTag = new CompoundTag();
        topicTag.putString("scientia.topic", topic);
        topicTag.putBoolean("scientia.completed", true);
        ItemStack stack = new ItemStack(ScientiaItems.RESEARCH_NOTES.get(), 1);
        stack.setTag(topicTag);
        return stack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isAdvancedResearchRecipe() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScientiaRecipes.ADVANCED_RESEARCH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AdvancedResearchTableRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "advanced_research";
    }
}
