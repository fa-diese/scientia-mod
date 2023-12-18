package net.fadiese.scientia;

import com.mojang.logging.LogUtils;
import net.fadiese.scientia.client.gui.CollationTableScreen;
import net.fadiese.scientia.client.gui.ResearchTableScreen;
import net.fadiese.scientia.client.tooltip.*;
import net.fadiese.scientia.registry.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Scientia.MOD_ID)
public class Scientia {
    public static final String MOD_ID = "scientia";

    public static final TagKey<Item> RESEARCH_TOOLS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MOD_ID, "research_tools"));
    public static final Logger LOGGER = LogUtils.getLogger();

    public Scientia() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ScientiaBlocks.register(modEventBus);
        ScientiaItems.register(modEventBus);
        ScientiaBlockEntities.register(modEventBus);
        ScientiaMenuTypes.register(modEventBus);
        ScientiaRecipes.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ScientiaFunctions.register();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ScientiaMenuTypes.RESEARCH_TABLE_CONTAINER.get(), ResearchTableScreen::new);
            MenuScreens.register(ScientiaMenuTypes.COLLATION_TABLE_CONTAINER.get(), CollationTableScreen::new);
        }

        @SubscribeEvent
        public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
            event.register((notes, layer) -> layer != 1 ? -1 : notes.hasTag() ? notes.getTag().getInt("scientia.color") : -1, ScientiaItems.RESEARCH_NOTES.get());
            event.register((notes, layer) -> layer != 1 ? -1 : notes.hasTag() ? notes.getTag().getInt("scientia.color") : -1, ScientiaItems.FAILED_EXPERIMENT_NOTE.get());
        }

        @SubscribeEvent
        public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(TopicTooltip.class, tooltip -> new ClientTopicTooltip(tooltip.topic()));
            event.register(CompatibleTopicsTooltip.class, tooltip -> new ClientCompatibleTopicsTooltip(tooltip.compatibleTopics()));
            event.register(ItemListTooltip.class, tooltip -> new ClientItemListTooltip(tooltip.items()));
        }
    }
}
