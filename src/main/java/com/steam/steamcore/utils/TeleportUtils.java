package com.steam.steamcore.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class TeleportUtils {

    /**
     * Teleports the player to their bed/anchor respawn point,
     * or to the world spawn if no respawn is set.
     */
    public static void teleportToRespawn(ServerPlayer player) {
        MinecraftServer server = player.server;
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos target = resolveRespawnPos(player, overworld);

        player.teleportTo(
                overworld,
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }

    /**
     * Teleports the player to a specific dimension and position.
     */
    public static void teleportTo(ServerPlayer player, ServerLevel level, BlockPos pos) {
        player.teleportTo(
                level,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }

    /**
     * Resolves the best respawn position for a player:
     * bed/anchor → world spawn.
     */
    public static BlockPos resolveRespawnPos(ServerPlayer player, ServerLevel overworld) {
        BlockPos respawnPos = player.getRespawnPosition();
        return (respawnPos != null) ? respawnPos : overworld.getSharedSpawnPos();
    }

    /**
     * Finds a safe standing position in a level by scanning downward from maxY.
     * Returns null if nothing found.
     */
    public static BlockPos findSafePosition(ServerLevel level, int x, int z, int maxY) {
        int clampedMaxY = Math.min(maxY, level.getMaxBuildHeight() - 2);

        for (int y = clampedMaxY; y > level.getMinBuildHeight(); y--) {
            BlockPos pos = new BlockPos(x, y, z);

            boolean floorSolid = !level.getBlockState(pos).isAir()
                    && !level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.BEDROCK);
            boolean headClear = level.getBlockState(pos.above()).isAir()
                    && level.getBlockState(pos.above(2)).isAir();

            if (floorSolid && headClear) {
                return pos.above();
            }
        }

        return null;
    }
}
