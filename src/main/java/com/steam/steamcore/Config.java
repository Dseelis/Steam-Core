package com.steam.steamcore;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ===== GENERAL =====
    public static final ModConfigSpec.BooleanValue SHOW_INTRO_MESSAGES =
            BUILDER.comment("Show intro messages when entering the world for the first time")
                    .define("showIntroMessages", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean showIntroMessages;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        showIntroMessages = SHOW_INTRO_MESSAGES.get();
    }
}