package com.example.rpgcore;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class ClientEvents {
    public static final KeyMapping STATUS_KEY = new KeyMapping(
            "key.rpgcore.status",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_N,
            "key.categories.rpgcore");

    @EventBusSubscriber(modid = RpgCore.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent e) {
            e.register(STATUS_KEY);
        }
    }

    @EventBusSubscriber(modid = RpgCore.MODID, value = Dist.CLIENT)
    public static class GameBus {
        @SubscribeEvent
        public static void onTick(ClientTickEvent.Post e) {
            Minecraft mc = Minecraft.getInstance();
            while (STATUS_KEY.consumeClick()) {
                if (mc.screen == null && mc.player != null) {
                    mc.setScreen(new StatusScreen());
                }
            }
        }
    }
}
