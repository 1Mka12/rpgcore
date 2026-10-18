package com.example.rpgcore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RpgData(int level, int xp, int points,
                      int str, int vit, int per, int agi, int intl) {
    public static final int STATS = 5;

    public static final Codec<RpgData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("level").forGetter(RpgData::level),
            Codec.INT.fieldOf("xp").forGetter(RpgData::xp),
            Codec.INT.optionalFieldOf("points", 5).forGetter(RpgData::points),
            Codec.INT.optionalFieldOf("str", 0).forGetter(RpgData::str),
            Codec.INT.optionalFieldOf("vit", 0).forGetter(RpgData::vit),
            Codec.INT.optionalFieldOf("per", 0).forGetter(RpgData::per),
            Codec.INT.optionalFieldOf("agi", 0).forGetter(RpgData::agi),
            Codec.INT.optionalFieldOf("intl", 0).forGetter(RpgData::intl)
    ).apply(i, (a, b, c, d, e, f, g, h) -> new RpgData(a, b, c, d, e, f, g, h)));

    public RpgData(int level, int xp) {
        this(level, xp, 5, 0, 0, 0, 0, 0);
    }

    public int xpNeeded() {
        return level * 100;
    }

    public int get(int s) {
        return switch (s) {
            case 0 -> str;
            case 1 -> vit;
            case 2 -> per;
            case 3 -> agi;
            default -> intl;
        };
    }

    public RpgData withStat(int s, int v) {
        return switch (s) {
            case 0 -> new RpgData(level, xp, points, v, vit, per, agi, intl);
            case 1 -> new RpgData(level, xp, points, str, v, per, agi, intl);
            case 2 -> new RpgData(level, xp, points, str, vit, v, agi, intl);
            case 3 -> new RpgData(level, xp, points, str, vit, per, v, intl);
            default -> new RpgData(level, xp, points, str, vit, per, agi, v);
        };
    }

    public RpgData withPoints(int p) {
        return new RpgData(level, xp, p, str, vit, per, agi, intl);
    }

    public RpgData withProgress(int lvl, int x, int pts) {
        return new RpgData(lvl, x, pts, str, vit, per, agi, intl);
    }
}
