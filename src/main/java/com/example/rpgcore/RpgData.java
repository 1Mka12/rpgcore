package com.example.rpgcore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RpgData(int level, int xp) {
    public static final Codec<RpgData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("level").forGetter(RpgData::level),
            Codec.INT.fieldOf("xp").forGetter(RpgData::xp)
    ).apply(i, RpgData::new));

    public int xpNeeded() {
        return level * 100;
    }
}
