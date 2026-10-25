package com.steam.steamcore;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // GENERAL
    public static final ModConfigSpec.BooleanValue SHOW_INTRO_MESSAGES;
    public static final ModConfigSpec.BooleanValue SHOW_MENU_BUTTON;
    public static final ModConfigSpec.BooleanValue ENABLE_UPDATE_NOTIFICATIONS;
    public static final ModConfigSpec.BooleanValue ENABLE_ABYSS;
    public static final ModConfigSpec.IntValue ABYSS_HEIGHT;
    public static final ModConfigSpec.IntValue ABYSS_DAMAGE;
    public static final ModConfigSpec.BooleanValue ENABLE_PORTALS;
    public static final ModConfigSpec.DoubleValue DISASSEMBLY_FAIL_CHANCE;
    public static final ModConfigSpec.BooleanValue ENABLE_DISASSEMBLY_SOUND;

    // DEBUG
    public static final ModConfigSpec.BooleanValue ENABLE_STEAMDEBUG_COMMAND;

    // DEV
    public static final ModConfigSpec.BooleanValue ENABLE_WIP_ITEMS;

    // STORY
    public static final ModConfigSpec.BooleanValue ENABLE_STORY;
    public static final ModConfigSpec.BooleanValue ENABLE_CLOUDS_SPAWN;
    public static final ModConfigSpec.BooleanValue ENABLE_MECHANIC_NPC;
    public static final ModConfigSpec.BooleanValue ENABLE_BOSS_PROGRESSION;
    public static final ModConfigSpec.IntValue MOB_MAX_LEVEL;
    public static final ModConfigSpec.DoubleValue MOB_HEALTH_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue MOB_DAMAGE_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue MOB_SPEED_PER_LEVEL;

    static {

        // GENERAL
        BUILDER.push("general");

        SHOW_INTRO_MESSAGES = BUILDER
                .comment("Show intro messages when entering the world")
                .define("showIntroMessages", false);

        SHOW_MENU_BUTTON = BUILDER
                .comment("Show the SteamCreate 2 info button on the main menu")
                .define("showMenuButton", true);

        ENABLE_UPDATE_NOTIFICATIONS = BUILDER
                .comment("Show update notifications when a new version is available")
                .define("enableUpdateNotifications", true);

        ENABLE_PORTALS = BUILDER
                .comment("Allow Nether portals")
                .define("enablePortals", true);

        DISASSEMBLY_FAIL_CHANCE = BUILDER
                .comment("Chance of failed disassembly (0.0 - 1.0)")
                .defineInRange("disassemblyFailChance", 0.15, 0.0, 1.0);

        ENABLE_DISASSEMBLY_SOUND = BUILDER
                .comment("Enable disassembly table sound")
                .define("enableDisassemblySound", true);

        BUILDER.pop();


        // DEBUG
        BUILDER.push("debug");

        ENABLE_STEAMDEBUG_COMMAND = BUILDER
                .comment("Enable /steamdebug")
                .define("enableSteamDebugCommand", true);

        BUILDER.pop();

        // DEV
        BUILDER.push("dev");

        ENABLE_WIP_ITEMS = BUILDER
                .comment("Enable Work In Progress items/features")
                .define("enableWipItems", true);

        ENABLE_ABYSS = BUILDER
                .comment("Enable abyss teleport system")
                .define("enableAbyss", false);

        ABYSS_HEIGHT = BUILDER
                .comment("Y level where teleport triggers")
                .defineInRange("abyssHeight", -62, -256, 0);

        ABYSS_DAMAGE = BUILDER
                .comment("Damage dealt before teleport")
                .defineInRange("abyssDamage", 4, 0, 40);

        BUILDER.pop();
 
         // STORY
         BUILDER.push("story");

         ENABLE_STORY = BUILDER
                 .comment("Enable story progression and all story-related features")
                 .define("enableStory", true);

         ENABLE_CLOUDS_SPAWN = BUILDER
                 .comment("Enable initial player spawn in the clouds dimension")
                 .define("enableCloudsSpawn", true);

         ENABLE_MECHANIC_NPC = BUILDER
                 .comment("Enable the Mechanic NPC spawning in the clouds and overworld")
                 .define("enableMechanicNpc", true);

         ENABLE_BOSS_PROGRESSION = BUILDER
                 .comment("Enable boss progression and mob level scaling (default: false)")
                 .define("enableBossProgression", false);

         MOB_MAX_LEVEL = BUILDER
                 .comment("Maximum mob level scaling")
                 .defineInRange("mobMaxLevel", 10, 1, 100);

         MOB_HEALTH_PER_LEVEL = BUILDER
                 .comment("Bonus max health multiplier per level above 1 (0.10 = +10%)")
                 .defineInRange("mobHealthPerLevel", 0.10, 0.0, 10.0);

         MOB_DAMAGE_PER_LEVEL = BUILDER
                 .comment("Bonus attack damage multiplier per level above 1 (0.07 = +7%)")
                 .defineInRange("mobDamagePerLevel", 0.07, 0.0, 10.0);

         MOB_SPEED_PER_LEVEL = BUILDER
                 .comment("Bonus movement speed multiplier per level above 1 (0.015 = +1.5%)")
                 .defineInRange("mobSpeedPerLevel", 0.015, 0.0, 1.0);

         BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}