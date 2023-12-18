package net.fadiese.scientia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextColor;

public record ResearchTopic(String topic, TextColor color, int experience) {
    public static final Codec<ResearchTopic> CODEC = RecordCodecBuilder.create(researchTopicInstance -> researchTopicInstance.group(
            Codec.STRING.fieldOf("topic").forGetter(ResearchTopic::topic),
            TextColor.CODEC.fieldOf("color").orElse(TextColor.fromRgb(13816533)).forGetter(ResearchTopic::color),
            Codec.INT.fieldOf("experience").orElse(0).forGetter(ResearchTopic::experience)
    ).apply(researchTopicInstance, ResearchTopic::new));
}
