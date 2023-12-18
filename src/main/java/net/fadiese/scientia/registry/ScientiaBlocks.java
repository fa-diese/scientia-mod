package net.fadiese.scientia.registry;

import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.block.*;
import net.fadiese.scientia.item.itemgroup.ScientiaItemGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ScientiaBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Scientia.MOD_ID);

    public static final RegistryObject<Block> RESEARCH_TABLE = registerBlock("research_table", () -> new ResearchTableBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP));

    public static final RegistryObject<Block> COLLATION_TABLE = registerBlock("collation_table", () -> new CollationTableBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP));

    public static final RegistryObject<Block> TEST_TUBE_RACK = registerBlock("test_tube_rack", () -> new TestTubeRackBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP).durability(100));

    public static final RegistryObject<Block> MICROSCOPE = registerBlock("microscope", () -> new MicroscopeBlock(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.COPPER).instabreak().noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP).durability(100));

    public static final RegistryObject<Block> RETORT = registerBlock("retort", () -> new RetortBlock(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).instabreak().noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP).durability(100));

    public static final RegistryObject<Block> CRUCIBLE = registerBlock("crucible", () -> new CrucibleBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).instabreak().noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP).durability(100));

    public static final RegistryObject<Block> DRAFTING_BOARD = registerBlock("drafting_board", () -> new DraftingBoardBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP).durability(100));

    public static final RegistryObject<Block> DISSECTION_TOOLS = registerBlock("dissection_tools", () -> new DissectionToolsBlock(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).instabreak().noOcclusion()), new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP).durability(100));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties properties) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        registerBlockItem(name, result, properties);
        return result;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, Item.Properties properties) {
        return ScientiaItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
