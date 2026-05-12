package com.steam.steamcore;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // GENERAL
    public static final ModConfigSpec.BooleanValue SHOW_INTRO_MESSAGES;
    public static final ModConfigSpec.BooleanValue ENABLE_ABYSS;
    public static final ModConfigSpec.IntValue ABYSS_HEIGHT;
    public static final ModConfigSpec.IntValue ABYSS_DAMAGE;
    public static final ModConfigSpec.BooleanValue ENABLE_PORTALS;
    public static final ModConfigSpec.BooleanValue ENABLE_GAMMA_IGNITE;
    public static final ModConfigSpec.DoubleValue DISASSEMBLY_FAIL_CHANCE;

    // DEBUG
    public static final ModConfigSpec.BooleanValue ENABLE_STEAMDEBUG_COMMAND;

    static {

        // GENERAL
        BUILDER.push("general");

        SHOW_INTRO_MESSAGES = BUILDER
                .comment("Show intro messages when entering the world")
                .define("showIntroMessages", false);

        ENABLE_ABYSS = BUILDER
                .comment("Enable abyss teleport system")
                .define("enableAbyss", false);

        ENABLE_GAMMA_IGNITE = BUILDER
                .comment("Enable Gamma Ignite item usage")
                .define("enableGammaIgnite", true);

        DISASSEMBLY_FAIL_CHANCE = BUILDER
                .comment("Chance of failed disassembly (0.0 - 1.0)")
                .defineInRange("disassemblyFailChance", 0.15, 0.0, 1.0);

        ABYSS_HEIGHT = BUILDER
                .comment("Y level where teleport triggers")
                .defineInRange("abyssHeight", -62, -256, 0);

        ABYSS_DAMAGE = BUILDER
                .comment("Damage dealt before teleport")
                .defineInRange("abyssDamage", 4, 0, 40);

        ENABLE_PORTALS = BUILDER
                .comment("Allow Nether portals")
                .define("enablePortals", true);

        BUILDER.pop();


        // DEBUG
        BUILDER.push("debug");

        ENABLE_STEAMDEBUG_COMMAND = BUILDER
                .comment("Enable /steamdebug")
                .define("enableSteamDebugCommand", false);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}