package com.steam.steamcore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TeleportUtils {

    // Teleports the player to their bed/anchor respawn point,
    // or to the world spawn if no respawn is set.
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

    // Teleports the player to a specific dimension and position.
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

    // Resolves the best respawn position for a player:
    // bed/anchor → world spawn.
    public static BlockPos resolveRespawnPos(ServerPlayer player, ServerLevel overworld) {
        BlockPos respawnPos = player.getRespawnPosition();
        return (respawnPos != null) ? respawnPos : overworld.getSharedSpawnPos();
    }

    // Finds a safe standing position by scanning downward from maxY.
    // "Safe" means: solid non-hazardous floor, two air blocks above, player won't
    // land in lava or liquid.
    // Returns null if nothing found within the column.
    public static BlockPos findSafePosition(ServerLevel level, int x, int z, int maxY) {
        int clampedMaxY = Math.min(maxY, level.getMaxBuildHeight() - 2);

        for (int y = clampedMaxY; y > level.getMinBuildHeight(); y--) {
            BlockPos floor = new BlockPos(x, y, z);
            BlockState floorState = level.getBlockState(floor);

            // Floor must be solid, not bedrock, not liquid, not lava/fire hazard
            boolean solidFloor = !floorState.isAir()
                    && !floorState.is(Blocks.BEDROCK)
                    && !floorState.is(Blocks.LAVA)
                    && !floorState.is(Blocks.WATER)
                    && !floorState.is(Blocks.FIRE)
                    && !floorState.is(Blocks.SOUL_FIRE)
                    && !floorState.is(Blocks.MAGMA_BLOCK)
                    && floorState.getFluidState().isEmpty();

            if (!solidFloor) continue;

            // The two blocks the player occupies must be air
            BlockPos feet = floor.above();
            BlockPos head = floor.above(2);
            boolean clearAbove = level.getBlockState(feet).isAir()
                    && level.getBlockState(head).isAir();

            if (clearAbove) {
                return feet; // return the position the player's feet will be at
            }
        }

        return null;
    }
}
