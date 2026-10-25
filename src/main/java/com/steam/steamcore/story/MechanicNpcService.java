package com.steam.steamcore.story;

import com.steam.steamcore.Config;
import com.steam.steamcore.story.dialogue.DialogueDefinition;
import com.steam.steamcore.story.dialogue.DialogueService;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;

/** Spawns the story mechanic as a protected, stationary villager in both story locations. */
public final class MechanicNpcService {
    public static final String NPC_TAG = "steamcore_mechanic_npc";
    private static final BlockPos CLOUDS_PLATFORM = new BlockPos(0, 128, 0);

    private MechanicNpcService() {}

    public static void ensureCloudsMechanic(ServerLevel level) {
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_MECHANIC_NPC.get()) {
            removeMechanic(level, CLOUDS_PLATFORM);
            return;
        }
        ensure(level, CLOUDS_PLATFORM, DialogueDefinition.MECHANIC_INTRO_ID, true);
    }

    public static void ensureOverworldMechanic(ServerLevel level) {
        BlockPos workshopPos = workshopPlayerPosition(level);
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_MECHANIC_NPC.get()) {
            removeMechanic(level, workshopPos);
            return;
        }
        ensure(level, workshopPos, DialogueDefinition.MECHANIC_MAIN_ID, false);
    }

    public static void removeMechanic(ServerLevel level, BlockPos base) {
        AABB search = new AABB(base).inflate(32, 16, 32);
        var existing = level.getEntitiesOfClass(Villager.class, search,
                villager -> villager.getTags().contains(NPC_TAG));
        for (Villager villager : existing) {
            villager.discard();
        }
    }

    public static void prepareCloudsMechanic(ServerLevel level) {
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_MECHANIC_NPC.get()) return;
        Villager npc = findCloudsMechanic(level);
        if (npc == null) return;
        npc.setPos(5.5, 129.0, 0.5);
        npc.setPose(Pose.STANDING);
        npc.addTag("steamcore_awakening_npc");
    }

    public static void finishCloudsMechanic(ServerLevel level) {
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_MECHANIC_NPC.get()) return;
        Villager npc = findCloudsMechanic(level);
        if (npc == null) return;
        npc.setPos(3.5, 129.0, 0.5);
        npc.setYRot(90.0F);
        npc.setPose(Pose.STANDING);
        npc.removeTag("steamcore_awakening_npc");
    }

    public static void bringCloudsMechanicTo(ServerLevel level, BlockPos playerPosition) {
        if (!Config.ENABLE_STORY.get() || !Config.ENABLE_MECHANIC_NPC.get()) return;
        ensureCloudsMechanic(level);
        Villager npc = findCloudsMechanic(level);
        if (npc == null) return;

        BlockPos npcPosition = TeleportUtils.findSafePosition(level,
                playerPosition.getX() + 2, playerPosition.getZ(), playerPosition.getY());
        if (npcPosition == null) npcPosition = playerPosition.east(2);
        npc.teleportTo(npcPosition.getX() + 0.5, npcPosition.getY(), npcPosition.getZ() + 0.5);
        npc.setYRot(180.0F);
        npc.setPose(Pose.STANDING);
    }

    private static Villager findCloudsMechanic(ServerLevel level) {
        AABB search = new AABB(CLOUDS_PLATFORM).inflate(24, 8, 24);
        return level.getEntitiesOfClass(Villager.class, search,
                villager -> villager.getTags().contains(NPC_TAG)).stream().findFirst().orElse(null);
    }

    public static BlockPos workshopPlayerPosition(ServerLevel overworld) {
        BlockPos spawn = overworld.getSharedSpawnPos();
        BlockPos safe = findStandPosition(overworld, spawn.getX(), spawn.getZ());
        return safe == null ? spawn.above() : safe;
    }

    public static Villager spawnScriptNpc(ServerLevel level, double x, double y, double z,
                                           String displayName, String dialogueId) {
        if (dialogueId == null || displayName == null
                || com.steam.steamcore.story.dialogue.DialogueRegistry.get(dialogueId) == null) return null;
        Villager npc = EntityType.VILLAGER.create(level);
        if (npc == null) return null;
        npc.moveTo(x, y, z, 0.0F, 0.0F);
        npc.setCustomName(displayName.startsWith("$")
                ? Component.translatable(displayName.substring(1))
                : Component.literal(displayName));
        npc.setCustomNameVisible(true);
        npc.setNoAi(true);
        npc.setInvulnerable(true);
        npc.setSilent(true);
        npc.setPersistenceRequired();
        npc.getPersistentData().putString(DialogueService.ENTITY_DIALOGUE_KEY, dialogueId);
        level.addFreshEntity(npc);
        return npc;
    }

    private static void ensure(ServerLevel level, BlockPos base, String dialogueId, boolean clouds) {
        AABB search = new AABB(base).inflate(24, 8, 24);
        var existing = level.getEntitiesOfClass(Villager.class, search, villager -> villager.getTags().contains(NPC_TAG));
        if (!existing.isEmpty()) {
            Villager npc = existing.getFirst();
            npc.getPersistentData().putString(DialogueService.ENTITY_DIALOGUE_KEY, dialogueId);
            if (clouds) npc.setPos(5.5, 129.0, 0.5);
            return;
        }

        Villager mechanic = EntityType.VILLAGER.create(level);
        if (mechanic == null) return;
        BlockPos stand = findStandPosition(level, base.getX() + (clouds ? 5 : 3), base.getZ());
        if (stand == null) stand = base.above();
        mechanic.moveTo(stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5, 90.0F, 0.0F);
        mechanic.setCustomName(Component.translatable("entity.steamcore.mechanic"));
        mechanic.setCustomNameVisible(true);
        mechanic.setNoAi(true);
        mechanic.setInvulnerable(true);
        mechanic.setSilent(true);
        mechanic.setPersistenceRequired();
        mechanic.addTag(NPC_TAG);
        mechanic.getPersistentData().putString(DialogueService.ENTITY_DIALOGUE_KEY, dialogueId);
        level.addFreshEntity(mechanic);
    }

    private static BlockPos findStandPosition(ServerLevel level, int x, int z) {
        int maxY = Math.min(level.getMaxBuildHeight() - 2, Math.max(level.getHeight() - 1, 200));
        return TeleportUtils.findSafePosition(level, x, z, maxY);
    }
}
