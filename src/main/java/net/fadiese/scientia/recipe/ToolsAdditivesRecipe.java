package net.fadiese.scientia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fadiese.scientia.registry.ScientiaItems;
import net.fadiese.scientia.registry.ScientiaRecipes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ToolsAdditivesRecipe(ResourceLocation id, Item tool,
                                   List<Item> additives) implements Recipe<SimpleContainer> {

    public static Codec<ToolsAdditivesRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Registry.ITEM.byNameCodec().fieldOf("tool").forGetter(ToolsAdditivesRecipe::tool),
                Registry.ITEM.byNameCodec().listOf().fieldOf("additives").forGetter(ToolsAdditivesRecipe::additives)
        ).apply(instance, ToolsAdditivesRecipe::new));
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, Level pLevel) {
        return !pLevel.isClientSide()
                && (((pContainer.getItem(4).is(ScientiaItems.RESEARCH_NOTES.get()) && pContainer.getItem(5).is(tool)))
                || (!pContainer.getItem(4).is(ScientiaItems.RESEARCH_NOTES.get()) && pContainer.getItem(2).is(tool)));
    }

    public boolean hasAdditive(@NotNull SimpleContainer pContainer) {
        return (pContainer.getItem(4).is(ScientiaItems.RESEARCH_NOTES.get()) && additives.contains(pContainer.getItem(6).getItem())) ||
                (!pContainer.getItem(4).is(ScientiaItems.RESEARCH_NOTES.get()) && additives.contains(pContainer.getItem(3).getItem()));
    }

    @Override
    public ItemStack assemble(@NotNull SimpleContainer pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScientiaRecipes.TOOLS_ADDITIVES_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ToolsAdditivesRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "tools_additives";
    }
}
