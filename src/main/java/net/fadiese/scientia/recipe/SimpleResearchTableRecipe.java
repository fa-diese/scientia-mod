package net.fadiese.scientia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.data.ResearchFailure;
import net.fadiese.scientia.data.ResearchTopic;
import net.fadiese.scientia.data.ScientiaCodecs;
import net.fadiese.scientia.registry.ScientiaItems;
import net.fadiese.scientia.registry.ScientiaRecipes;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record SimpleResearchTableRecipe(ResourceLocation id, List<Ingredient> possibleSubjects,
                                        Map<Item, List<Item>> properToolsAdditives, int duration,
                                        Map<Item, Map<String, ResearchFailure>> failures,
                                        ResearchTopic result) implements IResearchRecipe {
    public static Codec<SimpleResearchTableRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                ScientiaCodecs.INGREDIENT_CODEC.listOf().fieldOf("possibleSubjects").forGetter(SimpleResearchTableRecipe::possibleSubjects),
                Codec.unboundedMap(Registry.ITEM.byNameCodec(), Registry.ITEM.byNameCodec().listOf()).fieldOf("properToolsAdditives").forGetter(SimpleResearchTableRecipe::properToolsAdditives),
                Codec.INT.fieldOf("duration").orElse(1200).forGetter(SimpleResearchTableRecipe::duration),
                Codec.unboundedMap(Registry.ITEM.byNameCodec(), Codec.unboundedMap(Codec.STRING, ResearchFailure.CODEC)).fieldOf("failures").forGetter(SimpleResearchTableRecipe::failures),
                ResearchTopic.CODEC.fieldOf("result").forGetter(SimpleResearchTableRecipe::result)
        ).apply(instance, SimpleResearchTableRecipe::new));
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return !pLevel.isClientSide() && possibleSubjects.stream().anyMatch(elt -> elt.test(pContainer.getItem(0)))
                && pContainer.getItem(1).is(Items.PAPER)
                && pContainer.getItem(2).is(Scientia.RESEARCH_TOOLS)
                && pContainer.getItem(4).isEmpty()
                && pContainer.getItem(7).isEmpty();
    }

    @Override
    public ItemStack assemble(@NotNull SimpleContainer pContainer) {
        if (!hasProperToolsAdditives(pContainer)) {
            return new ItemStack(ScientiaItems.CRUMPLED_PAPER_BALL.get(), 1);
        }
        return getResultItem();
    }

    private boolean hasProperToolsAdditives(SimpleContainer pContainer) {
        List<Item> properAdditives = properToolsAdditives.get(pContainer.getItem(2).getItem());
        return properAdditives != null && (properAdditives.isEmpty() || properAdditives.contains(pContainer.getItem(3).getItem()));
    }

    public ResearchFailure getFailure(SimpleContainer pContainer) {
        if (!hasProperToolsAdditives(pContainer)) {
            Map<String, ResearchFailure> failuresForTool = failures.get(pContainer.getItem(2).getItem());
            if (failuresForTool != null && !failuresForTool.isEmpty()) {
                return failuresForTool.getOrDefault(Registry.ITEM.getKey(pContainer.getItem(3).getItem()).toString(), failuresForTool.get("default"));
            }
        }
        return null;
    }

    public String topic() {
        return result.topic();
    }

    public int experience() {
        return result.experience();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        CompoundTag topicTag = new CompoundTag();
        topicTag.putString("scientia.topic", result.topic());
        topicTag.putBoolean("scientia.completed", false);
        topicTag.putInt("scientia.color", result.color().getValue());
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
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScientiaRecipes.SIMPLE_RESEARCH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SimpleResearchTableRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "simple_research";
    }
}
