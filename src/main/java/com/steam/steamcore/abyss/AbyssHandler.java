package com.steam.steamcore.abyss;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public class AbyssHandler {

    // ABYSS SYSTEM
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;
        if (!Config.ENABLE_ABYSS.get()) return;

        var tag = player.getPersistentData();

        // Tick down abyss cooldown
        int abyssCooldown = tag.getInt("steamcore_abyss_cooldown");
        if (abyssCooldown > 0) {
            tag.putInt("steamcore_abyss_cooldown", abyssCooldown - 1);
        }

        // Protect freshly-joined players from instant trigger
        if (!tag.contains("steamcore_join_protection")) {
            tag.putInt("steamcore_join_protection", AbyssConstants.JOIN_PROTECTION_TICKS);
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

        var tag = player.getPersistentData();
        if (tag.getInt("steamcore_abyss_cooldown") > 0) return;

        ServerLevel nether = player.server.getLevel(Level.NETHER);
        if (nether == null) return;

        // Effects & sound in the current dimension before teleport
        player.hurt(player.damageSources().magic(), Config.ABYSS_DAMAGE.get());
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1));
        player.playNotifySound(
                SoundEvents.AMBIENT_CAVE.value(),
                SoundSource.PLAYERS, 1f, 0.5f);

        // Find safe Nether landing — use shared utility (checks lava/liquids)
        BlockPos safePos = TeleportUtils.findSafePosition(
                nether,
                player.getBlockX(),
                player.getBlockZ(),
                AbyssConstants.NETHER_SAFE_SCAN_MAX_Y);

        if (safePos == null) {
            safePos = new BlockPos(player.getBlockX(), 64, player.getBlockZ());
        }

        player.teleportTo(
                nether,
                safePos.getX() + 0.5,
                safePos.getY(),
                safePos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot());

        player.fallDistance = 0;
        player.invulnerableTime = AbyssConstants.POST_TELEPORT_INVULN_TICKS;
        tag.putInt("steamcore_abyss_cooldown", AbyssConstants.ABYSS_COOLDOWN_TICKS);

        // Particles appear in the Nether after the player has arrived
        nether.sendParticles(
                ParticleTypes.SMOKE,
                safePos.getX() + 0.5,
                safePos.getY() + 1,
                safePos.getZ() + 0.5,
                40, 0.5, 1, 0.5, 0.01);

        player.sendSystemMessage(
                Component.literal("The Abyss consumed you...")
                        .withStyle(ChatFormatting.DARK_RED));
    }

    // NETHER EXIT — send player back to Overworld if they reach the roof
    private static void handleNetherExit(ServerPlayer player) {
        if (player.level().dimension() != Level.NETHER) return;
        if (player.getBlockY() < AbyssConstants.NETHER_ROOF_EXIT_Y) return;
        if (player.getPersistentData().getInt("steamcore_abyss_cooldown") > 0) return;

        // teleportToRespawn handles both bed-spawn and world-spawn fallback
        TeleportUtils.teleportToRespawn(player);
    }

    // DISABLE PORTALS
    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        if (!Config.ENABLE_PORTALS.get()) {
            event.setCanceled(true);
        }
    }
}
