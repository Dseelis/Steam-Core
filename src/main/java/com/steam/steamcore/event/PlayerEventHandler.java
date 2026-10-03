package com.steam.steamcore.event;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.command.DebugCommand;
import com.steam.steamcore.item.GammaIgniteItem;
import com.steam.steamcore.network.StoryPayloads;
import com.steam.steamcore.story.StoryService;
import com.steam.steamcore.story.StoryWorldData;
import com.steam.steamcore.story.MechanicNpcService;
import com.steam.steamcore.utils.PlayerDataKeys;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SteamCore.MODID)
public class PlayerEventHandler {

    private static final ResourceKey<Level> CLOUDS_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "clouds")
    );
    private static final BlockPos CLOUDS_PLATFORM_CENTER = new BlockPos(0, 128, 0);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DebugCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel overworld = player.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld == null) return;

        var tag = player.getPersistentData();
        StoryWorldData storyData = StoryWorldData.get(player.server);

        if (storyData.isDifficultyChosen()) {
            StoryService.grantMechanicBeacon(player);
            MechanicNpcService.ensureOverworldMechanic(overworld);
        }

        if (!storyData.isDifficultyChosen()) {
            ServerLevel clouds = player.getServer().getLevel(CLOUDS_DIMENSION);
            if (clouds != null) {
                createCloudsLandingPlatform(clouds);
                MechanicNpcService.ensureCloudsMechanic(clouds);
                if (!tag.getBoolean(PlayerDataKeys.INTRO_PENDING)) {
                    TeleportUtils.teleportTo(player, clouds, CLOUDS_PLATFORM_CENTER.above());
                    tag.putBoolean(PlayerDataKeys.INTRO_PENDING, true);
                    tag.putBoolean(PlayerDataKeys.SPAWNED, true);
                } else if (!player.level().dimension().equals(CLOUDS_DIMENSION)) {
                    TeleportUtils.teleportTo(player, clouds, CLOUDS_PLATFORM_CENTER.above());
                }

                if (tag.getBoolean(PlayerDataKeys.INTRO_PENDING)
                        && player.level().dimension().equals(CLOUDS_DIMENSION)) {
                    com.steam.steamcore.story.dialogue.DialogueService.openAwakening(
                            player, com.steam.steamcore.story.dialogue.DialogueDefinition.MECHANIC_INTRO_ID);
                }
            } else {
                // Keep a safe fallback if a world was created before this dimension was added.
                if (!tag.getBoolean(PlayerDataKeys.SPAWNED)) {
                    BlockPos spawn = overworld.getSharedSpawnPos();
                    int searchStartY = Math.min(overworld.getMaxBuildHeight() - 2, 200);
                    BlockPos safePos = TeleportUtils.findSafePosition(
                            overworld, spawn.getX(), spawn.getZ(), searchStartY);
                    if (safePos == null) safePos = spawn.above();
                    TeleportUtils.teleportTo(player, overworld, safePos);
                    tag.putBoolean(PlayerDataKeys.SPAWNED, true);
                }
                SteamCore.LOGGER.error("Clouds dimension {} is missing; sent first-join player {} to the Overworld.",
                        CLOUDS_DIMENSION.location(), player.getGameProfile().getName());
            }
        } else if (tag.getBoolean(PlayerDataKeys.INTRO_PENDING)) {
            tag.putBoolean(PlayerDataKeys.INTRO_PENDING, false);
            StoryService.sendToOverworld(player);
        } else if (!tag.getBoolean(PlayerDataKeys.SPAWNED)) {
            StoryService.sendToOverworld(player);
            tag.putBoolean(PlayerDataKeys.SPAWNED, true);
        }
    }

    private static void createCloudsLandingPlatform(ServerLevel level) {
        // Create this only once. Returning players must not have a rebuilt platform
        // overwrite blocks they may have placed in the starting dimension.
        if (!level.getBlockState(CLOUDS_PLATFORM_CENTER).isAir()) return;

        for (int x = -6; x <= 6; x++) {
            for (int z = -6; z <= 6; z++) {
                if (x * x + z * z > 42) continue;
                BlockPos pos = CLOUDS_PLATFORM_CENTER.offset(x, 0, z);
                level.setBlockAndUpdate(pos, Blocks.WHITE_WOOL.defaultBlockState());
            }
        }

        // A small mechanical landing pad gives the future mechanic a clear meeting point.
        for (int x = -2; x <= 2; x++) {
            level.setBlockAndUpdate(CLOUDS_PLATFORM_CENTER.offset(x, 0, -3),
                    Blocks.IRON_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(CLOUDS_PLATFORM_CENTER.offset(x, 0, 3),
                    Blocks.IRON_BLOCK.defaultBlockState());
        }
        for (int z = -2; z <= 2; z++) {
            level.setBlockAndUpdate(CLOUDS_PLATFORM_CENTER.offset(-3, 0, z),
                    Blocks.IRON_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(CLOUDS_PLATFORM_CENTER.offset(3, 0, z),
                    Blocks.IRON_BLOCK.defaultBlockState());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        GammaIgniteItem.cancelPendingTeleport(player.getUUID());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        SteamCore.LOGGER.info("HELLO from server starting");
    }
}
