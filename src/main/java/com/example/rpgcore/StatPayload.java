package com.example.rpgcore;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StatPayload(int stat, int delta) implements CustomPacketPayload {
    public static final Type<StatPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RpgCore.MODID, "stat"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StatPayload> CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeVarInt(p.stat());
                        buf.writeVarInt(p.delta());
                    },
                    buf -> new StatPayload(buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StatPayload p, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer sp) {
            RpgEvents.changeStat(sp, p.stat(), p.delta());
        }
    }
}
