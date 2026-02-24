package com.steam.steamcore;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // GENERAL
    public static final ModConfigSpec.BooleanValue SHOW_INTRO_MESSAGES;

    // DEBUG
    public static final ModConfigSpec.BooleanValue ENABLE_MODLIST_COMMAND;
    public static final ModConfigSpec.BooleanValue ENABLE_PACKINFO_COMMAND;

    static {

        // GENERAL
        BUILDER.push("general");

        SHOW_INTRO_MESSAGES = BUILDER
                .comment("Show intro messages when entering the world")
                .define("showIntroMessages", false);

        BUILDER.pop();

        // DEBUG CATEGORY
        BUILDER.push("debug");

        ENABLE_MODLIST_COMMAND = BUILDER
                .comment("Enable /generatemodlist command")
                .define("enableModlistCommand", false);
        ENABLE_PACKINFO_COMMAND = BUILDER
                .comment("Enable /packinfo command")
                .define("enablePackinfoCommand", false);


        BUILDER.pop();

    }

    public static final ModConfigSpec SPEC = BUILDER.build();

}