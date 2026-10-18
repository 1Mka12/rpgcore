package com.example.rpgcore;

import net.minecraft.client.Minecraft;

public class ClientHandler {
    public static void handle(SyncPayload p) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.setData(RpgCore.RPG, new RpgData(p.level(), p.xp()));
        }
    }
}
