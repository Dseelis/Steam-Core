package com.steam.steamcore.abyss;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.utils.PlayerDataKeys;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;
        if (!Config.ENABLE_ABYSS.get()) return;

        var tag = player.getPersistentData();

        // Tick down abyss cooldown
        int abyssCooldown = tag.getInt(PlayerDataKeys.ABYSS_COOLDOWN);
        if (abyssCooldown > 0) {
            tag.putInt(PlayerDataKeys.ABYSS_COOLDOWN, abyssCooldown - 1);
        }

        // Protect freshly-joined players from instant trigger
        if (!tag.contains(PlayerDataKeys.JOIN_PROTECTION)) {
            tag.putInt(PlayerDataKeys.JOIN_PROTECTION, AbyssConstants.JOIN_PROTECTION_TICKS);
            return;
        }
        int joinCooldown = tag.getInt(PlayerDataKeys.JOIN_PROTECTION);
        if (joinCooldown > 0) {
            tag.putInt(PlayerDataKeys.JOIN_PROTECTION, joinCooldown - 1);
            return;
        }

        handleAbyss(player);
        handleNetherExit(player);
    }

    private static void handleAbyss(ServerPlayer player) {
        if (player.level().dimension() != Level.OVERWORLD) return;
        if (player.getY() >= Config.ABYSS_HEIGHT.get()) return;

        var tag = player.getPersistentData();
        if (tag.getInt(PlayerDataKeys.ABYSS_COOLDOWN) > 0) return;

        ServerLevel nether = player.server.getLevel(Level.NETHER);
        if (nether == null) return;

        player.hurt(player.damageSources().magic(), Config.ABYSS_DAMAGE.get());
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1));
        player.playNotifySound(
                SoundEvents.AMBIENT_CAVE.value(),
                SoundSource.PLAYERS, 1f, 0.5f);

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
        tag.putInt(PlayerDataKeys.ABYSS_COOLDOWN, AbyssConstants.ABYSS_COOLDOWN_TICKS);

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

    private static void handleNetherExit(ServerPlayer player) {
        if (player.level().dimension() != Level.NETHER) return;
        if (player.getBlockY() < AbyssConstants.NETHER_ROOF_EXIT_Y) return;
        if (player.getPersistentData().getInt(PlayerDataKeys.ABYSS_COOLDOWN) > 0) return;

        TeleportUtils.teleportToRespawn(player);
    }

    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        if (!Config.ENABLE_PORTALS.get()) {
            event.setCanceled(true);
        }
    }
}
