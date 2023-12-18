package net.fadiese.scientia.registry;

import net.fadiese.scientia.loot.SetToolsDamageFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class ScientiaFunctions {

    public static final LootItemFunctionType SET_TOOL_DAMAGE = new LootItemFunctionType(new SetToolsDamageFunction.Serializer());

    public static void register() {
        Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation("scientia", "set_tools_damage"), SET_TOOL_DAMAGE);
    }

}
