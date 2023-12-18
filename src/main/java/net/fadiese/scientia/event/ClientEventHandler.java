package net.fadiese.scientia.event;

import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.data.ResearchTopic;
import net.fadiese.scientia.helper.ResearchToolsHelper;
import net.fadiese.scientia.helper.TopicHelper;
import net.fadiese.scientia.recipe.CollationTableRecipe;
import net.fadiese.scientia.recipe.SimpleResearchTableRecipe;
import net.fadiese.scientia.recipe.ToolsAdditivesRecipe;
import net.fadiese.scientia.registry.ScientiaRecipes;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Scientia.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onRecipeUpdated(RecipesUpdatedEvent event) {
        List<ResearchTopic> topics = event.getRecipeManager().getAllRecipesFor(ScientiaRecipes.SIMPLE_RESEARCH_TYPE.get()).stream().map(SimpleResearchTableRecipe::result).toList();
        List<CollationTableRecipe> collationRecipes = event.getRecipeManager().getAllRecipesFor(ScientiaRecipes.COLLATION_TYPE.get());
        List<ToolsAdditivesRecipe> toolsAdditivesRecipes = event.getRecipeManager().getAllRecipesFor(ScientiaRecipes.TOOLS_ADDITIVES_TYPE.get());
        TopicHelper.initializeInstance(topics, collationRecipes);
        ResearchToolsHelper.initializeInstanceWithAdditives(toolsAdditivesRecipes);
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        List<Item> items = new ArrayList<>();
        event.getRegistryAccess().registry(Registry.ITEM_REGISTRY).get().getTagOrEmpty(Scientia.RESEARCH_TOOLS).forEach(elt -> {
            items.add(elt.value());
        });
        ResearchToolsHelper.initializeInstanceWithToolsList(items);
    }
}
