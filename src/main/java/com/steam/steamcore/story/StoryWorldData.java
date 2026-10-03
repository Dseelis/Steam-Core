package com.steam.steamcore.story;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.HashSet;
import java.util.Set;

public final class StoryWorldData extends SavedData {
    private static final String DATA_NAME = "steamcore_story";
    private static final Factory<StoryWorldData> FACTORY = new Factory<>(
            StoryWorldData::new,
            StoryWorldData::load,
            DataFixTypes.LEVEL
    );

    private boolean difficultyChosen;
    private boolean mobProgressionEnabled;
    private int bossProgressPoints;
    private final Set<String> countedBosses = new HashSet<>();

    public static StoryWorldData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public boolean isDifficultyChosen() {
        return difficultyChosen;
    }

    public boolean isMobProgressionEnabled() {
        return mobProgressionEnabled;
    }

    public int getBossProgressPoints() {
        return bossProgressPoints;
    }

    public boolean recordBossDefeat(String entityId, int points) {
        if (points <= 0 || !countedBosses.add(entityId)) return false;
        bossProgressPoints += points;
        setDirty();
        return true;
    }

    public void addBossProgressPoints(int points) {
        if (points <= 0) return;
        bossProgressPoints = Math.min(Integer.MAX_VALUE - points, bossProgressPoints) + points;
        setDirty();
    }

    public void chooseMobProgression(boolean enabled) {
        this.difficultyChosen = true;
        this.mobProgressionEnabled = enabled;
        setDirty();
    }

    private static StoryWorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        StoryWorldData data = new StoryWorldData();
        data.difficultyChosen = tag.getBoolean("difficulty_chosen");
        data.mobProgressionEnabled = tag.getBoolean("mob_progression_enabled");
        data.bossProgressPoints = Math.max(0, tag.contains("boss_progress_points")
                ? tag.getInt("boss_progress_points")
                : tag.getInt("boss_fragment_count"));
        ListTag countedBosses = tag.getList("counted_bosses", 8);
        for (int i = 0; i < countedBosses.size(); i++) {
            data.countedBosses.add(countedBosses.getString(i));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("difficulty_chosen", difficultyChosen);
        tag.putBoolean("mob_progression_enabled", mobProgressionEnabled);
        tag.putInt("boss_progress_points", bossProgressPoints);
        ListTag countedBosses = new ListTag();
        for (String entityId : this.countedBosses) countedBosses.add(StringTag.valueOf(entityId));
        tag.put("counted_bosses", countedBosses);
        return tag;
    }
}
