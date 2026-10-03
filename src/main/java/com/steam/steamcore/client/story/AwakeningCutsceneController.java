package com.steam.steamcore.client.story;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.Villager;

/** Drives first-person camera, smoke and hand motion for the 3D cloud-world scene. */
public final class AwakeningCutsceneController {
    private static boolean active;
    private static int ticks;
    private static boolean reached;

    private AwakeningCutsceneController() {}

    public static void start() {
        active = true;
        ticks = 0;
        reached = false;
    }

    public static void stop() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    public static void tick() {
        if (!active) return;
        ticks++;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        Villager mechanic = minecraft.level.getEntitiesOfClass(Villager.class,
                minecraft.player.getBoundingBox().inflate(12), entity -> entity.getTags().contains("steamcore_mechanic_npc"))
                .stream().findFirst().orElse(null);
        if (mechanic != null && ticks > 26 && ticks < 116 && ticks % 3 == 0) {
            for (int i = 0; i < 3; i++) {
                minecraft.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        mechanic.getX() + (minecraft.level.random.nextDouble() - 0.5) * 0.9,
                        mechanic.getY() + minecraft.level.random.nextDouble() * 1.6,
                        mechanic.getZ() + (minecraft.level.random.nextDouble() - 0.5) * 0.9,
                        0.0, 0.025 + minecraft.level.random.nextDouble() * 0.025, 0.0);
            }
        }
        if (!reached && ticks >= 110) {
            minecraft.player.swing(InteractionHand.MAIN_HAND);
            reached = true;
        }
    }

    public static CameraPose cameraPose(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player == null) return null;
        float time = ticks + partialTick;
        float rise = smooth((time - 112.0F) / 38.0F);
        float settle = (float) Math.sin(time * 0.08F) * (1.0F - rise) * 0.025F;
        double cameraY = player.getY() + 0.42 + rise * 1.20 + settle;
        float lookUp = smooth((time - 28.0F) / 50.0F);
        float pitch = 16.0F - lookUp * 22.0F + rise * 2.0F;
        float yaw = 270.0F + (float) Math.sin(time * 0.025F) * (1.0F - rise) * 1.2F;
        return new CameraPose(player.getX(), cameraY, player.getZ(), yaw, pitch);
    }

    private static float smooth(float value) {
        float clamped = Math.max(0.0F, Math.min(1.0F, value));
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    public record CameraPose(double x, double y, double z, float yaw, float pitch) {}
}
