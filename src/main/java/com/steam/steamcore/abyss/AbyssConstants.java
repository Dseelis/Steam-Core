package com.steam.steamcore.abyss;

public final class AbyssConstants {

    private AbyssConstants() {}

    // Y level in the Nether above which the player gets sent back to Overworld.
    public static final int NETHER_ROOF_EXIT_Y = 123;

    // Max Y to scan downward when finding a safe Nether landing spot.
    public static final int NETHER_SAFE_SCAN_MAX_Y = 120;

    // Invulnerability ticks applied after Abyss teleport.
    public static final int POST_TELEPORT_INVULN_TICKS = 10;

    // Cooldown ticks after Abyss trigger to prevent instant re-trigger.
    public static final int ABYSS_COOLDOWN_TICKS = 40;

    // Join protection ticks to prevent Abyss from hitting on first spawn.
    public static final int JOIN_PROTECTION_TICKS = 40;
}
