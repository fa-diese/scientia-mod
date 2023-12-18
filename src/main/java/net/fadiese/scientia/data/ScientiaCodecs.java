package net.fadiese.scientia.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.crafting.Ingredient;

public class ScientiaCodecs {
    public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(ScientiaCodecs::decodeIngredient, ScientiaCodecs::encodeIngredient);

    private static DataResult<Ingredient> decodeIngredient(Dynamic<?> dynamic) {
        return DataResult.success(Ingredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue()));
    }

    private static Dynamic<JsonElement> encodeIngredient(Ingredient ingredient) {
        return new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()).convert(JsonOps.COMPRESSED);
    }
}
