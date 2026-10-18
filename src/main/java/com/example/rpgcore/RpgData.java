package com.example.rpgcore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RpgData(int level, int xp) {
    public static final Codec<RpgData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("level").forGetter(RpgData::level),
            Codec.INT.fieldOf("xp").forGetter(RpgData::xp)
    ).apply(i, RpgData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RpgData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, RpgData::level,
                    ByteBufCodecs.VAR_INT, RpgData::xp,
                    RpgData::new);

    public int xpNeeded() {
        return level * 100;
    }
}
