package com.example.rpgcore;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(RpgCore.MODID)
public class RpgCore {
    public static final String MODID = "rpgcore";

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<RpgData>> RPG = ATTACHMENTS.register("rpg",
            () -> AttachmentType.builder(() -> new RpgData(1, 0))
                    .serialize(RpgData.CODEC)
                    .copyOnDeath()
                    .build());

    public RpgCore(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
        modBus.addListener(SyncPayload::register);
    }
}
