package net.fadiese.scientia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fadiese.scientia.data.ItemResult;
import net.fadiese.scientia.registry.ScientiaItems;
import net.fadiese.scientia.registry.ScientiaRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CollationTableRecipe(ResourceLocation id, String firstTopic, String secondTopic,
                                   double chancesSimpleTopic, double chancesAdvancedTopic, double bonusChancesFailures,
                                   int duration,
                                   int knowledgeLevel,
                                   int levelCost, ItemResult result) implements Recipe<SimpleContainer> {

    public static Codec<CollationTableRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("firstTopic").forGetter(CollationTableRecipe::firstTopic),
                Codec.STRING.fieldOf("secondTopic").forGetter(CollationTableRecipe::secondTopic),
                Codec.doubleRange(0, 1).fieldOf("chancesSimpleTopic").forGetter(CollationTableRecipe::chancesSimpleTopic),
                Codec.doubleRange(0, 1).fieldOf("chancesAdvancedTopic").forGetter(CollationTableRecipe::chancesAdvancedTopic),
                Codec.doubleRange(0, 1).fieldOf("bonusChancesFailures").forGetter(CollationTableRecipe::bonusChancesFailures),
                Codec.INT.fieldOf("duration").orElse(1200).forGetter(CollationTableRecipe::duration),
                Codec.INT.fieldOf("knowledgeLevel").orElse(0).forGetter(CollationTableRecipe::knowledgeLevel),
                Codec.INT.fieldOf("levelCost").orElse(0).forGetter(CollationTableRecipe::levelCost),
                ItemResult.CODEC.fieldOf("result").forGetter(CollationTableRecipe::result)
        ).apply(instance, CollationTableRecipe::new));
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (!pLevel.isClientSide()
                && pContainer.getItem(6).isEmpty()
                && pContainer.getItem(0).is(ScientiaItems.RESEARCH_NOTES.get()) && pContainer.getItem(0).hasTag()
                && pContainer.getItem(1).is(ScientiaItems.RESEARCH_NOTES.get()) && pContainer.getItem(1).hasTag()
                && pContainer.getItem(2).is(Items.BOOK)) {
            String topic1 = pContainer.getItem(0).getTag().getString("scientia.topic");
            String topic2 = pContainer.getItem(1).getTag().getString("scientia.topic");
            return (firstTopic.matches(topic1) && secondTopic.matches(topic2)) || (firstTopic.matches(topic2) && secondTopic.matches(topic1));
        }
        return false;
    }

    @Override
    public ItemStack assemble(@NotNull SimpleContainer pContainer) {
        double random = Math.random();
        double threshold = getThresholdChances(pContainer);
        if (random <= threshold) {
            return getResultItem();
        } else if (random <= threshold + 0.2) {
            if (!pContainer.getItem(0).getTag().getBoolean("scientia.completed")) {
                pContainer.getItem(0).getTag().putBoolean("scientia.completed", true);
                return pContainer.getItem(0);
            } else if (!pContainer.getItem(1).getTag().getBoolean("scientia.completed")) {
                pContainer.getItem(1).getTag().putBoolean("scientia.completed", true);
                return pContainer.getItem(1);
            } else {
                return Math.random() >= 0.5 ? pContainer.getItem(1) : pContainer.getItem(0);
            }
        }
        return ItemStack.EMPTY;
    }

    public double getThresholdChances(@NotNull SimpleContainer pContainer) {
        double threshold = pContainer.getItem(0).getTag().getBoolean("scientia.completed") ? chancesAdvancedTopic : chancesSimpleTopic;
        threshold *= pContainer.getItem(1).getTag().getBoolean("scientia.completed") ? chancesAdvancedTopic : chancesSimpleTopic;
        ItemStack note1 = pContainer.getItem(3);
        ItemStack note2 = pContainer.getItem(4);
        ItemStack note3 = pContainer.getItem(5);
        if (note1.hasTag()
                && (firstTopic.equals(note1.getTag().getString("scientia.topic")) || secondTopic.equals(note1.getTag().getString("scientia.topic")))) {
            threshold += bonusChancesFailures;
        }
        if (note2.hasTag()
                && (firstTopic.equals(note2.getTag().getString("scientia.topic")) || secondTopic.equals(note2.getTag().getString("scientia.topic")))
                && (!note1.hasTag() || haveDifferentScientiaTags(note1, note2))) {
            threshold += bonusChancesFailures;
        }
        if (note3.hasTag()
                && (firstTopic.equals(note3.getTag().getString("scientia.topic")) || secondTopic.equals(note3.getTag().getString("scientia.topic")))
                && ((!note1.hasTag() || haveDifferentScientiaTags(note1, note3))
                && (!note2.hasTag() || haveDifferentScientiaTags(note2, note3)))) {
            threshold += bonusChancesFailures;
        }
        return threshold;
    }

    private boolean haveDifferentScientiaTags(ItemStack note1, ItemStack note2) {
        return !note1.getTag().getString("scientia.topic").equals(note2.getTag().getString("scientia.topic"))
                || !note1.getTag().getString("scientia.tool").equals(note2.getTag().getString("scientia.tool"))
                || !note1.getTag().getString("scientia.additive").equals(note2.getTag().getString("scientia.additive"));
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        ItemStack resultItem = new ItemStack(result.item(), 1);
        resultItem.setTag(result.nbt().copy());
        return resultItem;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScientiaRecipes.COLLATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CollationTableRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "collation";
    }
}
