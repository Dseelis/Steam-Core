package com.steam.steamcore.integration.kubejs;

import com.steam.steamcore.story.StoryWorldData;
import com.steam.steamcore.story.dialogue.DialogueRegistry;
import com.steam.steamcore.story.dialogue.DialogueService;
import com.steam.steamcore.story.MechanicNpcService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Public scripting facade, bound into KubeJS as SteamCoreStory. */
public final class SteamCoreKubeJSApi {
    private SteamCoreKubeJSApi() {}

    public static void setMobProgressionEnabled(Level level, boolean enabled) {
        if (level == null || level.isClientSide || level.getServer() == null) return;
        StoryWorldData.get(level.getServer()).chooseMobProgression(enabled);
    }

    public static boolean isMobProgressionEnabled(Level level) {
        return level != null && !level.isClientSide && level.getServer() != null
                && StoryWorldData.get(level.getServer()).isMobProgressionEnabled();
    }

    public static int getBossProgressPoints(Level level) {
        return level == null || level.isClientSide || level.getServer() == null ? 0
                : StoryWorldData.get(level.getServer()).getBossProgressPoints();
    }

    public static void addBossProgressPoints(Level level, int points) {
        if (level == null || level.isClientSide || level.getServer() == null || points <= 0) return;
        StoryWorldData.get(level.getServer()).addBossProgressPoints(points);
    }

    public static boolean recordBossDefeat(Level level, String bossId, int points) {
        if (level == null || level.isClientSide || level.getServer() == null || bossId == null || points <= 0) return false;
        return StoryWorldData.get(level.getServer()).recordBossDefeat(bossId, points);
    }

    public static void registerDialogue(String id, String json) {
        DialogueRegistry.register(id, json);
    }

    public static boolean openDialogue(ServerPlayer player, String id) {
        return player != null && DialogueService.open(player, id);
    }

    public static void setNpcDialogue(Entity entity, String id) {
        if (entity == null || id == null || DialogueRegistry.get(id) == null) return;
        entity.getPersistentData().putString(DialogueService.ENTITY_DIALOGUE_KEY, id);
    }

    public static Entity spawnNpc(Level level, double x, double y, double z, String displayName, String dialogueId) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel) || level.isClientSide) return null;
        return MechanicNpcService.spawnScriptNpc(serverLevel, x, y, z, displayName, dialogueId);
    }

    public static String getNpcDialogue(Entity entity) {
        return entity == null ? "" : entity.getPersistentData().getString(DialogueService.ENTITY_DIALOGUE_KEY);
    }

    public static void clearNpcDialogue(Entity entity) {
        if (entity != null) entity.getPersistentData().remove(DialogueService.ENTITY_DIALOGUE_KEY);
    }
}
