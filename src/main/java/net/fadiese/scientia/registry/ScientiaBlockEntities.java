package net.fadiese.scientia.registry;

import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.blockentity.CollationTableBlockEntity;
import net.fadiese.scientia.blockentity.ResearchTableBlockEntity;
import net.fadiese.scientia.blockentity.ToolBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ScientiaBlockEntities {

    private static Block[] tools() {
        return new Block[]{
                ScientiaBlocks.CRUCIBLE.get(),
                ScientiaBlocks.DISSECTION_TOOLS.get(),
                ScientiaBlocks.DRAFTING_BOARD.get(),
                ScientiaBlocks.MICROSCOPE.get(),
                ScientiaBlocks.RETORT.get(),
                ScientiaBlocks.TEST_TUBE_RACK.get()
        };
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Scientia.MOD_ID);
    public static final RegistryObject<BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE = BLOCK_ENTITIES.register("research_table", () -> BlockEntityType.Builder.of(ResearchTableBlockEntity::new, ScientiaBlocks.RESEARCH_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CollationTableBlockEntity>> COLLATION_TABLE = BLOCK_ENTITIES.register("collation_table", () -> BlockEntityType.Builder.of(CollationTableBlockEntity::new, ScientiaBlocks.COLLATION_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ToolBlockEntity>> TOOL_ENTITY = BLOCK_ENTITIES.register("tool_entity", () -> BlockEntityType.Builder.of(ToolBlockEntity::new, tools()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
