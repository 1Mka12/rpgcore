package com.example.rpgcore;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record SyncPayload(int level, int xp) implements CustomPacketPayload {
    public static final Type<SyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RpgCore.MODID, "sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SyncPayload::level,
                    ByteBufCodecs.VAR_INT, SyncPayload::xp,
                    SyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent e) {
        e.registrar("1").playToClient(TYPE, CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> ClientHandler.handle(payload)));
    }
}
