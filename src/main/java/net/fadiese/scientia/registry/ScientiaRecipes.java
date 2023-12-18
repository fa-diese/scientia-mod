package net.fadiese.scientia.registry;

import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.data.CodecRecipeSerializer;
import net.fadiese.scientia.recipe.AdvancedResearchTableRecipe;
import net.fadiese.scientia.recipe.CollationTableRecipe;
import net.fadiese.scientia.recipe.SimpleResearchTableRecipe;
import net.fadiese.scientia.recipe.ToolsAdditivesRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ScientiaRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Scientia.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Scientia.MOD_ID);

    public static final RegistryObject<RecipeType<ToolsAdditivesRecipe>> TOOLS_ADDITIVES_TYPE =
            RECIPE_TYPES.register("tools_additives", () -> ToolsAdditivesRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ToolsAdditivesRecipe>> TOOLS_ADDITIVES_SERIALIZER =
            SERIALIZERS.register("tools_additives", () -> new CodecRecipeSerializer<>(TOOLS_ADDITIVES_TYPE.get(), ToolsAdditivesRecipe::codec));

    public static final RegistryObject<RecipeType<SimpleResearchTableRecipe>> SIMPLE_RESEARCH_TYPE =
            RECIPE_TYPES.register("simple_research", () -> SimpleResearchTableRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<SimpleResearchTableRecipe>> SIMPLE_RESEARCH_SERIALIZER =
            SERIALIZERS.register("simple_research", () -> new CodecRecipeSerializer<>(SIMPLE_RESEARCH_TYPE.get(), SimpleResearchTableRecipe::codec));

    public static final RegistryObject<RecipeType<AdvancedResearchTableRecipe>> ADVANCED_RESEARCH_TYPE =
            RECIPE_TYPES.register("advanced_research", () -> AdvancedResearchTableRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<AdvancedResearchTableRecipe>> ADVANCED_RESEARCH_SERIALIZER =
            SERIALIZERS.register("advanced_research", () -> new CodecRecipeSerializer<>(ADVANCED_RESEARCH_TYPE.get(), AdvancedResearchTableRecipe::codec));

    public static final RegistryObject<RecipeType<CollationTableRecipe>> COLLATION_TYPE =
            RECIPE_TYPES.register("collation", () -> CollationTableRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<CollationTableRecipe>> COLLATION_SERIALIZER =
            SERIALIZERS.register("collation", () -> new CodecRecipeSerializer<>(COLLATION_TYPE.get(), CollationTableRecipe::codec));

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}
