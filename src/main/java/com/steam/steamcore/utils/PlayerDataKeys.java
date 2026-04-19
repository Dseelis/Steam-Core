package com.steam.steamcore.util;

/**
 * Central registry of all NBT persistent data keys used by SteamCore.
 * Never use raw strings outside this class.
 */
public final class PlayerDataKeys {

    private PlayerDataKeys() {}

    // Spawn / intro
    public static final String SPAWNED          = "steamcore_spawned";
    public static final String INTRO_SHOWN      = "steamcore_intro_shown";

    // Abyss system
    public static final String ABYSS_COOLDOWN   = "steamcore_abyss_cooldown";
    public static final String JOIN_PROTECTION  = "steamcore_join_protection";

    // Quest system (new)
    public static final String QUEST_STAGE      = "steamcore_quest_stage";
    public static final String QUEST_FLAGS      = "steamcore_quest_flags";
}
