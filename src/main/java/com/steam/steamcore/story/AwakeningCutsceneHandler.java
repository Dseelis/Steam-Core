package com.steam.steamcore.story;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.utils.PlayerDataKeys;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public final class AwakeningCutsceneHandler {
    public static final String START_TICK_KEY = "steamcore_awakening_start_tick";
    private AwakeningCutsceneHandler() {}

    public static void start(ServerPlayer player) {
        if (!com.steam.steamcore.Config.ENABLE_STORY.get() || !com.steam.steamcore.Config.ENABLE_CLOUDS_SPAWN.get() || !com.steam.steamcore.Config.ENABLE_MECHANIC_NPC.get()) return;
        player.getPersistentData().putLong(START_TICK_KEY, player.serverLevel().getGameTime());
        MechanicNpcService.prepareCloudsMechanic(player.serverLevel());
    }

    public static void finish(ServerPlayer player) {
        player.getPersistentData().remove(START_TICK_KEY);
        MechanicNpcService.finishCloudsMechanic(player.serverLevel());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!com.steam.steamcore.Config.ENABLE_STORY.get() || !com.steam.steamcore.Config.ENABLE_CLOUDS_SPAWN.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var data = player.getPersistentData();
        if (!data.contains(START_TICK_KEY) || !data.getBoolean(PlayerDataKeys.INTRO_PENDING)
                || !player.level().dimension().equals(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "clouds")))) return;

        long elapsed = player.serverLevel().getGameTime() - data.getLong(START_TICK_KEY);
        var npcs = player.serverLevel().getEntitiesOfClass(Villager.class,
                new AABB(player.blockPosition()).inflate(14, 5, 14), entity -> entity.getTags().contains(MechanicNpcService.NPC_TAG));
        if (npcs.isEmpty()) return;
        Villager npc = npcs.getFirst();

        if (elapsed < 112) {
            double progress = smooth(elapsed / 82.0);
            npc.setPos(5.5 - progress * 2.0, 129.0, 0.5);
            npc.setYRot(90.0F);
            npc.setPose(elapsed >= 74 ? Pose.CROUCHING : Pose.STANDING);
            if (elapsed == 108) npc.swing(InteractionHand.MAIN_HAND, true);
        } else {
            npc.setPos(3.5, 129.0, 0.5);
            npc.setYRot(90.0F);
            npc.setPose(Pose.STANDING);
            if (elapsed >= 168) finish(player);
        }
    }

    private static double smooth(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        return clamped * clamped * (3.0 - 2.0 * clamped);
    }
}
