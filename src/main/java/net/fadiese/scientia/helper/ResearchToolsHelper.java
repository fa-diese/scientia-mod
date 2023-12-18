package net.fadiese.scientia.helper;

import net.fadiese.scientia.client.tooltip.ItemListTooltip;
import net.fadiese.scientia.recipe.ToolsAdditivesRecipe;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResearchToolsHelper {

    private static ResearchToolsHelper instance;

    private ItemListTooltip toolsTooltip;

    private Map<Item, ItemListTooltip> additivesTooltips;

    private ResearchToolsHelper() {
    }

    public static void initializeInstanceWithToolsList(List<Item> tools) {
        if (instance == null) instance = new ResearchToolsHelper();
        instance.toolsTooltip = new ItemListTooltip(tools.stream().map(ItemStack::new).toList());
    }

    public static void initializeInstanceWithAdditives(List<ToolsAdditivesRecipe> toolsAdditivesRecipes) {
        if (instance == null) instance = new ResearchToolsHelper();
        instance.additivesTooltips = toolsAdditivesRecipes.stream().collect(Collectors.toMap(ToolsAdditivesRecipe::tool, elt -> new ItemListTooltip(elt.additives().stream().map(ItemStack::new).toList()), (existing, replacement) -> existing));
    }

    public static ResearchToolsHelper getInstance() {
        return instance;
    }

    public Optional<TooltipComponent> getResearchTools() {
        return Optional.of(toolsTooltip);
    }

    public Optional<TooltipComponent> getAdditives(Item tool) {
        return Optional.ofNullable(additivesTooltips.get(tool));
    }
}
