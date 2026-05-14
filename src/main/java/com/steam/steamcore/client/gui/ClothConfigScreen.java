package com.steam.steamcore.client.gui;

import com.steam.steamcore.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.steamcore.config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // GENERAL CATEGORY
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("steamcore.configuration.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("steamcore.configuration.showIntroMessages"), Config.SHOW_INTRO_MESSAGES.get())
                .setDefaultValue(false)
                .setSaveConsumer(Config.SHOW_INTRO_MESSAGES::set)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("steamcore.configuration.enablePortals"), Config.ENABLE_PORTALS.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.ENABLE_PORTALS::set)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("steamcore.configuration.disassemblyFailChance"), Config.DISASSEMBLY_FAIL_CHANCE.get())
                .setDefaultValue(0.15)
                .setMin(0.0)
                .setMax(1.0)
                .setTooltip(Component.translatable("steamcore.configuration.disassemblyFailChance.tooltip"))
                .setSaveConsumer(Config.DISASSEMBLY_FAIL_CHANCE::set)
                .build());

        // DEBUG CATEGORY
        ConfigCategory debug = builder.getOrCreateCategory(Component.translatable("steamcore.configuration.debug"));

        debug.addEntry(entryBuilder.startBooleanToggle(Component.translatable("steamcore.configuration.enableSteamDebugCommand"), Config.ENABLE_STEAMDEBUG_COMMAND.get())
                .setDefaultValue(true)
                .setTooltip(Component.translatable("steamcore.configuration.enableSteamDebugCommand.tooltip"))
                .setSaveConsumer(Config.ENABLE_STEAMDEBUG_COMMAND::set)
                .build());

        // DEV CATEGORY
        ConfigCategory dev = builder.getOrCreateCategory(Component.translatable("steamcore.configuration.dev"));

        dev.addEntry(entryBuilder.startBooleanToggle(Component.translatable("steamcore.configuration.enableWipItems"), Config.ENABLE_WIP_ITEMS.get())
                .setDefaultValue(true)
                .setTooltip(Component.literal("Enables Work In Progress items in the creative tab and JEI/EMI"))
                .setSaveConsumer(Config.ENABLE_WIP_ITEMS::set)
                .build());

        dev.addEntry(entryBuilder.startBooleanToggle(Component.translatable("steamcore.configuration.enableAbyss"), Config.ENABLE_ABYSS.get())
                .setDefaultValue(false)
                .setTooltip(Component.translatable("steamcore.configuration.enableAbyss.tooltip"))
                .setSaveConsumer(Config.ENABLE_ABYSS::set)
                .build());

        dev.addEntry(entryBuilder.startIntSlider(Component.translatable("steamcore.configuration.abyssHeight"), Config.ABYSS_HEIGHT.get(), -256, 0)
                .setDefaultValue(-62)
                .setTooltip(Component.translatable("steamcore.configuration.abyssHeight.tooltip"))
                .setSaveConsumer(Config.ABYSS_HEIGHT::set)
                .build());

        dev.addEntry(entryBuilder.startIntSlider(Component.translatable("steamcore.configuration.abyssDamage"), Config.ABYSS_DAMAGE.get(), 0, 40)
                .setDefaultValue(4)
                .setTooltip(Component.translatable("steamcore.configuration.abyssDamage.tooltip"))
                .setSaveConsumer(Config.ABYSS_DAMAGE::set)
                .build());

        builder.setSavingRunnable(() -> {
            Config.SPEC.save();
        });

        return builder.build();
    }
}
