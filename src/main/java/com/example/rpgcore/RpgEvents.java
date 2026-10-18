package com.example.rpgcore;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = RpgCore.MODID)
public class RpgEvents {
    private static final ResourceLocation HP_ID =
            ResourceLocation.fromNamespaceAndPath(RpgCore.MODID, "level_health");

    private static void applyStats(ServerPlayer p) {
        RpgData d = p.getData(RpgCore.RPG);
        var attr = p.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.addOrReplacePermanentModifier(new AttributeModifier(
                    HP_ID, (d.level() - 1) * 2.0, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void sync(ServerPlayer p) {
        RpgData d = p.getData(RpgCore.RPG);
        PacketDistributor.sendToPlayer(p, new SyncPayload(d.level(), d.xp()));
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent e) {
        if (e.getSource().getEntity() instanceof ServerPlayer p && e.getEntity() instanceof Enemy) {
            int gain = Math.max(1, (int) (e.getEntity().getMaxHealth() / 2));
            RpgData d = p.getData(RpgCore.RPG);
            int level = d.level();
            int xp = d.xp() + gain;
            boolean up = false;
            while (xp >= level * 100) {
                xp -= level * 100;
                level++;
                up = true;
            }
            p.setData(RpgCore.RPG, new RpgData(level, xp));
            p.displayClientMessage(Component.literal("+" + gain + " XP"), true);
            if (up) {
                applyStats(p);
                p.sendSystemMessage(Component.literal("Новий рівень: " + level + "!"));
            }
            sync(p);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            applyStats(p);
            sync(p);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            applyStats(p);
            sync(p);
        }
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) sync(p);
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("rpg").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayerOrException();
            RpgData d = p.getData(RpgCore.RPG);
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "Рівень " + d.level() + " | XP " + d.xp() + "/" + d.xpNeeded()), false);
            return 1;
        }));
    }
}
