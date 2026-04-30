package com.steam.steamcore.event;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.command.DebugCommand;
import com.steam.steamcore.item.GammaIgniteItem;
import com.steam.steamcore.utils.PlayerDataKeys;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public class PlayerEventHandler {

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

        if (!tag.getBoolean(PlayerDataKeys.SPAWNED)) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            int searchStartY = Math.min(overworld.getMaxBuildHeight() - 2, 200);

            BlockPos safePos = TeleportUtils.findSafePosition(
                    overworld, spawn.getX(), spawn.getZ(), searchStartY);

            if (safePos == null) {
                safePos = overworld.getSharedSpawnPos().above();
            }

            player.teleportTo(
                    overworld,
                    safePos.getX() + 0.5,
                    safePos.getY(),
                    safePos.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

            tag.putBoolean(PlayerDataKeys.SPAWNED, true);
        }

        if (Config.SHOW_INTRO_MESSAGES.get()
                && !tag.getBoolean(PlayerDataKeys.INTRO_SHOWN)) {

            player.sendSystemMessage(
                    Component.literal("Welcome to SteamCore Beta!")
                            .withStyle(ChatFormatting.GOLD)
            );
            player.sendSystemMessage(
                    Component.literal("You wake up in an unfamiliar world... How did you get here?")
                            .withStyle(ChatFormatting.GRAY)
            );

            tag.putBoolean(PlayerDataKeys.INTRO_SHOWN, true);
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
