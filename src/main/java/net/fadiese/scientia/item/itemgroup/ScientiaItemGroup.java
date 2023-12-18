package net.fadiese.scientia.item.itemgroup;

import net.fadiese.scientia.registry.ScientiaBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ScientiaItemGroup extends CreativeModeTab {

    public static ScientiaItemGroup SCIENTIA_GROUP = new ScientiaItemGroup("scientia_tab");

    public ScientiaItemGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ScientiaBlocks.TEST_TUBE_RACK.get());
    }

}
