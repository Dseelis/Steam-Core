package com.steam.steamcore.abyss;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.Config;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public class AbyssHandler {

    // ABYSS SYSTEM
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        if (!Config.ENABLE_ABYSS.get()) return;

        var tag = player.getPersistentData();

        int abyssCooldown = tag.getInt("steamcore_abyss_cooldown");
        if (abyssCooldown > 0) {
            tag.putInt("steamcore_abyss_cooldown", abyssCooldown - 1);
        }

        // Protect new joiners from instant Abyss deaths
        if (!tag.contains("steamcore_join_protection")) {
            tag.putInt("steamcore_join_protection", 40);
            return;
        }

        int joinCooldown = tag.getInt("steamcore_join_protection");
        if (joinCooldown > 0) {
            tag.putInt("steamcore_join_protection", joinCooldown - 1);
            return;
        }

        handleAbyss(player);
        handleNetherExit(player);
    }

    private static void handleAbyss(ServerPlayer player) {

        if (player.level().dimension() != Level.OVERWORLD) return;
        if (player.getY() >= Config.ABYSS_HEIGHT.get()) return;

        ServerLevel nether = player.server.getLevel(Level.NETHER);
        if (nether == null) return;

        // Damage
        player.hurt(player.damageSources().magic(), Config.ABYSS_DAMAGE.get());

        // Effect
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1));

        // Sound
        player.playNotifySound(
                SoundEvents.AMBIENT_CAVE.value(),
                SoundSource.PLAYERS,
                1f,
                0.5f
        );

        // Particles
        nether.sendParticles(
                ParticleTypes.SMOKE,
                player.getX(),
                player.getY(),
                player.getZ(),
                40,
                0.5, 1, 0.5,
                0.01
        );

        // Safe position in Nether
        BlockPos safePos = findSafeNetherPosition(nether, player.blockPosition());
        if (safePos == null) {
            // fallback
            safePos = new BlockPos(player.getBlockX(), 64, player.getBlockZ());
        }

        player.teleportTo(
                nether,
                safePos.getX() + 0.5,
                safePos.getY() + 1,
                safePos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
        player.fallDistance = 0;
        player.invulnerableTime = 10;
        player.getPersistentData().putInt("steamcore_abyss_cooldown", 40);

        player.sendSystemMessage(
                Component.literal("The Abyss consumed you...")
                        .withStyle(ChatFormatting.DARK_RED)
        );
    }

    // NETHER EXIT
    private static void handleNetherExit(ServerPlayer player) {

        if (player.level().dimension() != Level.NETHER) return;
        if (player.getBlockY() < 123) return;
        if (player.getPersistentData().getInt("steamcore_abyss_cooldown") > 0) {
            return;
        }

        MinecraftServer server = player.server;
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos respawnPos = player.getRespawnPosition();

        BlockPos target = (respawnPos != null)
                ? respawnPos
                : overworld.getSharedSpawnPos();

        player.teleportTo(
                overworld,
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()

        );
    }

    private static BlockPos findSafeNetherPosition(ServerLevel level, BlockPos originalPos) {

        int x = originalPos.getX();
        int z = originalPos.getZ();
        int maxY = Math.min(120, level.getMaxBuildHeight() - 2);

        for (int y = maxY; y > level.getMinBuildHeight(); y--) {

            BlockPos pos = new BlockPos(x, y, z);

            if (!level.getBlockState(pos).isAir()
                    && !level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.BEDROCK)
                    && level.getBlockState(pos.above()).isAir()
                    && level.getBlockState(pos.above(2)).isAir()) {

                return pos.above();
            }
        }

        return null;
    }

    public static void teleportToRespawn(ServerPlayer player) {

        MinecraftServer server = player.server;
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        if (overworld == null) return;

        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnPos != null) {

            player.teleportTo(
                    overworld,
                    respawnPos.getX() + 0.5,
                    respawnPos.getY(),
                    respawnPos.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

        } else {

            BlockPos spawn = overworld.getSharedSpawnPos();

            player.teleportTo(
                    overworld,
                    spawn.getX() + 0.5,
                    spawn.getY(),
                    spawn.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );
        }
    }

    // DISABLE PORTALS
    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        if (!Config.ENABLE_PORTALS.get()) {
            event.setCanceled(true);
        }
    }
}