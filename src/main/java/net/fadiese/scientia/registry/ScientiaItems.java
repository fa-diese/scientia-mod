package net.fadiese.scientia.registry;

import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.item.CrumpledPaperBallItem;
import net.fadiese.scientia.item.FailedExperimentNoteItem;
import net.fadiese.scientia.item.ResearchNotesItem;
import net.fadiese.scientia.item.itemgroup.ScientiaItemGroup;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ScientiaItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Scientia.MOD_ID);

    public static final RegistryObject<Item> RESEARCH_NOTES = ITEMS.register("research_notes",
            () -> new ResearchNotesItem(new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP)));
    public static final RegistryObject<Item> FAILED_EXPERIMENT_NOTE = ITEMS.register("failed_experiment_note",
            () -> new FailedExperimentNoteItem(new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP)));
    public static final RegistryObject<Item> CRUMPLED_PAPER_BALL = ITEMS.register("crumpled_paper_ball",
            () -> new CrumpledPaperBallItem(new Item.Properties().tab(ScientiaItemGroup.SCIENTIA_GROUP)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
