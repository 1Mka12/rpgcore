package com.example.rpgcore;

import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
    private static final int POINTS_PER_LEVEL = 2;

    private static void mod(ServerPlayer p, Holder<Attribute> attr, String id,
                            double amount, AttributeModifier.Operation op) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst != null) {
            inst.addOrReplacePermanentModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(RpgCore.MODID, id), amount, op));
        }
    }

    public static void applyStats(ServerPlayer p) {
        RpgData d = p.getData(RpgCore.RPG);
        mod(p, Attributes.MAX_HEALTH, "hp", (d.level() - 1) * 2.0 + d.vit(),
                AttributeModifier.Operation.ADD_VALUE);
        mod(p, Attributes.ATTACK_DAMAGE, "str", d.str() * 0.5,
                AttributeModifier.Operation.ADD_VALUE);
        mod(p, Attributes.LUCK, "per_luck", d.per() * 0.5,
                AttributeModifier.Operation.ADD_VALUE);
        mod(p, Attributes.BLOCK_INTERACTION_RANGE, "per_reach", d.per() * 0.2,
                AttributeModifier.Operation.ADD_VALUE);
        mod(p, Attributes.MOVEMENT_SPEED, "agi_speed", d.agi() * 0.02,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        mod(p, Attributes.ATTACK_SPEED, "agi_atk", d.agi() * 0.1,
                AttributeModifier.Operation.ADD_VALUE);
    }

    public static void sync(ServerPlayer p) {
        PacketDistributor.sendToPlayer(p, new SyncPayload(p.getData(RpgCore.RPG)));
    }

    public static void changeStat(ServerPlayer p, int stat, int delta) {
        if (stat < 0 || stat >= RpgData.STATS) return;
        RpgData d = p.getData(RpgCore.RPG);
        if (delta > 0) {
            if (d.points() <= 0) return;
            d = d.withStat(stat, d.get(stat) + 1).withPoints(d.points() - 1);
        } else if (delta < 0) {
            if (d.get(stat) <= 0) return;
            d = d.withStat(stat, d.get(stat) - 1).withPoints(d.points() + 1);
        } else {
            return;
        }
        p.setData(RpgCore.RPG, d);
        applyStats(p);
        sync(p);
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent e) {
        if (e.getSource().getEntity() instanceof ServerPlayer p && e.getEntity() instanceof Enemy) {
            RpgData d = p.getData(RpgCore.RPG);
            int base = Math.max(1, (int) (e.getEntity().getMaxHealth() / 2));
            int gain = Math.max(1, Math.round(base * (1f + 0.05f * d.intl())));
            int level = d.level();
            int xp = d.xp() + gain;
            int points = d.points();
            boolean up = false;
            while (xp >= level * 100) {
                xp -= level * 100;
                level++;
                points += POINTS_PER_LEVEL;
                up = true;
            }
            p.setData(RpgCore.RPG, d.withProgress(level, xp, points));
            p.displayClientMessage(Component.literal("+" + gain + " XP"), true);
            if (up) {
                applyStats(p);
                p.sendSystemMessage(Component.literal("Новий рівень: " + level + "! Очок навичок: " + points));
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
                    "Рівень " + d.level() + " | XP " + d.xp() + "/" + d.xpNeeded()
                            + " | Очок: " + d.points()), false);
            return 1;
        }));
    }
}
