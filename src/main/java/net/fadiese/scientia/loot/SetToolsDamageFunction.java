package net.fadiese.scientia.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fadiese.scientia.blockentity.ToolBlockEntity;
import net.fadiese.scientia.registry.ScientiaFunctions;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public class SetToolsDamageFunction extends LootItemConditionalFunction {
    protected SetToolsDamageFunction(LootItemCondition[] pPredicates) {
        super(pPredicates);
    }

    @Override
    protected ItemStack run(ItemStack pStack, @NotNull LootContext pContext) {
        if (!(pStack.getItem() instanceof BlockItem && pStack.isDamageableItem())) {
            return pStack;
        }
        BlockEntity blockEntity = pContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof ToolBlockEntity) {
            if (pStack.hurt(((ToolBlockEntity) blockEntity).getDamage(), pContext.getRandom(), null)) {
                return ItemStack.EMPTY;
            }
        }
        return pStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ScientiaFunctions.SET_TOOL_DAMAGE;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetToolsDamageFunction> {

        @Override
        public SetToolsDamageFunction deserialize(@NotNull JsonObject pObject, @NotNull JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            return new SetToolsDamageFunction(pConditions);
        }
    }
}
