package net.fadiese.scientia.registry;

import net.fadiese.scientia.Scientia;
import net.fadiese.scientia.inventory.CollationTableMenu;
import net.fadiese.scientia.inventory.ResearchTableMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ScientiaMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Scientia.MOD_ID);

    public static final RegistryObject<MenuType<ResearchTableMenu>> RESEARCH_TABLE_CONTAINER =
            registerMenuType(ResearchTableMenu::new, "research_table_container");

    public static final RegistryObject<MenuType<CollationTableMenu>> COLLATION_TABLE_CONTAINER =
            registerMenuType(CollationTableMenu::new, "collation_table_container");


    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                  String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
