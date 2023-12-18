package net.fadiese.scientia.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fadiese.scientia.Scientia;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class CodecRecipeSerializer<R extends Recipe<?>> implements RecipeSerializer<R> {
    private static final Gson GSON = new Gson();
    private final RecipeType<R> recipeType;
    private final Function<ResourceLocation, Codec<R>> codecInitializer;

    public CodecRecipeSerializer(RecipeType<R> recipeType, Function<ResourceLocation, Codec<R>> codecInitializer) {
        this.recipeType = recipeType;
        this.codecInitializer = codecInitializer;
    }

    @Override
    public R fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        return codecInitializer.apply(pRecipeId).parse(JsonOps.INSTANCE, pSerializedRecipe).getOrThrow(false, s -> Scientia.LOGGER.error("Could not parse " + pRecipeId.toString()));
    }

    @Override
    public @Nullable R fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        Optional<R> result = codecInitializer.apply(pRecipeId).parse(JsonOps.COMPRESSED, GSON.fromJson(pBuffer.readUtf(), JsonArray.class)).result();
        return result.orElse(null);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, R pRecipe) {
        codecInitializer.apply(pRecipe.getId()).encodeStart(JsonOps.COMPRESSED, pRecipe).result().ifPresent(element -> pBuffer.writeUtf(element.toString()));
    }
}
