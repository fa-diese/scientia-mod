package net.fadiese.scientia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public record ItemResult(Item item, CompoundTag nbt) {
    public static final Codec<ItemResult> CODEC = RecordCodecBuilder.create(collationResultInstance -> collationResultInstance.group(
            Registry.ITEM.byNameCodec().fieldOf("item").forGetter(ItemResult::item),
            CompoundTag.CODEC.fieldOf("nbt").orElse(new CompoundTag()).forGetter(ItemResult::nbt)
    ).apply(collationResultInstance, ItemResult::new));
}
