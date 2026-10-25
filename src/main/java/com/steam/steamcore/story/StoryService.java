package com.steam.steamcore.story;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.utils.PlayerDataKeys;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import com.steam.steamcore.registry.ModItems;
import com.steam.steamcore.story.MechanicNpcService;
import net.minecraft.world.item.ItemStack;

public final class StoryService {
    public static final String ENABLE_PROGRESSION = "difficulty_enable";
    public static final String DISABLE_PROGRESSION = "difficulty_disable";
    private static final ResourceKey<Level> CLOUDS_DIMENSION = ResourceKey.create(
            Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "clouds"));
    private static final BlockPos CLOUDS_RETURN_POSITION = new BlockPos(0, 129, 0);

    private StoryService() {}

    public static void chooseDifficulty(ServerPlayer player, String actionId) {
        if (!Config.ENABLE_STORY.get()) return;

        boolean enabled;
        if (ENABLE_PROGRESSION.equals(actionId)) {
            enabled = true;
        } else if (DISABLE_PROGRESSION.equals(actionId)) {
            enabled = false;
        } else {
            SteamCore.LOGGER.warn("Player {} sent unknown story dialogue action: {}",
                    player.getGameProfile().getName(), actionId);
            return;
        }

        StoryWorldData data = StoryWorldData.get(player.server);
        boolean introPending = player.getPersistentData().getBoolean(PlayerDataKeys.INTRO_PENDING);
        if (introPending && !player.level().dimension().equals(CLOUDS_DIMENSION)) return;
        if (introPending && data.isDifficultyChosen()) {
            player.getPersistentData().putBoolean(PlayerDataKeys.INTRO_PENDING, false);
            sendToOverworld(player);
            return;
        }
        data.chooseMobProgression(enabled);
        if (!introPending) {
            if (enabled && !Config.ENABLE_BOSS_PROGRESSION.get()) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        "message.steamcore.progression.disabled_in_config"));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                        enabled ? "message.steamcore.progression.enabled" : "message.steamcore.progression.disabled"));
            }
            return;
        }
        player.getPersistentData().putBoolean(PlayerDataKeys.INTRO_PENDING, false);
        player.getPersistentData().putBoolean(PlayerDataKeys.SPAWNED, true);
        player.getPersistentData().putBoolean(PlayerDataKeys.INTRO_SHOWN, true);
        giveMechanicBeacon(player);
        sendToOverworld(player);
    }

    public static void sendToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos spawn = overworld.getSharedSpawnPos();
        int searchStartY = Math.min(overworld.getMaxBuildHeight() - 2, 200);
        BlockPos safePos = TeleportUtils.findSafePosition(overworld, spawn.getX(), spawn.getZ(), searchStartY);
        if (safePos == null) safePos = spawn.above();
        TeleportUtils.teleportTo(player, overworld, safePos);
        if (Config.ENABLE_STORY.get() && Config.ENABLE_MECHANIC_NPC.get()) {
            giveMechanicBeacon(player);
            MechanicNpcService.ensureOverworldMechanic(overworld);
        }
    }

    public static boolean returnToClouds(ServerPlayer player) {
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_CLOUDS_SPAWN.get()) return false;
        ServerLevel clouds = player.server.getLevel(CLOUDS_DIMENSION);
        if (clouds == null) return false;

        if (Config.ENABLE_MECHANIC_NPC.get()) {
            MechanicNpcService.ensureCloudsMechanic(clouds);
        }
        TeleportUtils.teleportTo(player, clouds, CLOUDS_RETURN_POSITION);
        if (Config.ENABLE_MECHANIC_NPC.get()) {
            MechanicNpcService.bringCloudsMechanicTo(clouds, CLOUDS_RETURN_POSITION);
        }
        return true;
    }

    private static void giveMechanicBeacon(ServerPlayer player) {
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_MECHANIC_NPC.get()) return;
        var data = player.getPersistentData();
        if (data.getBoolean("steamcore_mechanic_beacon_given")) return;
        data.putBoolean("steamcore_mechanic_beacon_given", true);
        ItemStack beacon = new ItemStack(ModItems.MECHANIC_BEACON.get());
        if (!player.getInventory().add(beacon)) player.drop(beacon, false);
    }

    public static void grantMechanicBeacon(ServerPlayer player) {
        giveMechanicBeacon(player);
    }
}
