package net.fadiese.scientia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum ResearchFailureType {
    explosion,
    potion_effect,
    item;
    public static final Codec<ResearchFailureType> CODEC = Codec.STRING.comapFlatMap(value -> {
                try {
                    return DataResult.success(ResearchFailureType.valueOf(value));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(value + " is not a valid research failure value.");
                }
            },
            Enum::toString);
}
