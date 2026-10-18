package com.example.rpgcore;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record SyncPayload(RpgData data) implements CustomPacketPayload {
    public static final Type<SyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RpgCore.MODID, "sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        RpgData d = p.data();
                        buf.writeVarInt(d.level());
                        buf.writeVarInt(d.xp());
                        buf.writeVarInt(d.points());
                        buf.writeVarInt(d.str());
                        buf.writeVarInt(d.vit());
                        buf.writeVarInt(d.per());
                        buf.writeVarInt(d.agi());
                        buf.writeVarInt(d.intl());
                    },
                    buf -> new SyncPayload(new RpgData(
                            buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                            buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                            buf.readVarInt(), buf.readVarInt())));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent e) {
        PayloadRegistrar r = e.registrar("1");
        r.playToClient(TYPE, CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> ClientHandler.handle(payload)));
        r.playToServer(StatPayload.TYPE, StatPayload.CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> StatPayload.handle(payload, ctx)));
    }
}
