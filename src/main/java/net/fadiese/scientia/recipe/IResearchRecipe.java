package net.fadiese.scientia.recipe;

import net.fadiese.scientia.data.ResearchFailure;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public interface IResearchRecipe extends Recipe<SimpleContainer> {

    int duration();

    Map<Item, List<Item>> properToolsAdditives();

    Map<Item, Map<String, ResearchFailure>> failures();

    boolean isAdvancedResearchRecipe();

    String topic();

    int experience();

    ResearchFailure getFailure(SimpleContainer pContainer);

}
