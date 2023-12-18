package net.fadiese.scientia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fadiese.scientia.registry.ScientiaItems;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

public record ResearchFailure(ResearchFailureType type, int time, boolean generateNote, ItemResult item,
                              MobEffect effect, float radius, int amplifier) {
    public static final Codec<ResearchFailure> CODEC = RecordCodecBuilder.create(researchFailureInstance -> researchFailureInstance.group(
            ResearchFailureType.CODEC.fieldOf("type").forGetter(ResearchFailure::type),
            Codec.INT.fieldOf("time").forGetter(ResearchFailure::time),
            Codec.BOOL.fieldOf("generateNote").orElse(false).forGetter(ResearchFailure::generateNote),
            ItemResult.CODEC.fieldOf("item").orElse(new ItemResult(ScientiaItems.CRUMPLED_PAPER_BALL.get(), new CompoundTag())).forGetter(ResearchFailure::item),
            Registry.MOB_EFFECT.byNameCodec().fieldOf("effect").orElse(MobEffects.POISON).forGetter(ResearchFailure::effect),
            Codec.FLOAT.fieldOf("radius").orElse(3.0F).forGetter(ResearchFailure::radius),
            Codec.INT.fieldOf("amplifier").orElse(0).forGetter(ResearchFailure::amplifier)
    ).apply(researchFailureInstance, ResearchFailure::new));

    public ItemStack getItemStackResult() {
        ItemStack resultItem = new ItemStack(item.item(), 1);
        resultItem.setTag(item.nbt().copy());
        return resultItem;
    }
}
