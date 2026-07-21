package com.steam.steamcore;

import com.steam.steamcore.registry.ModBlockEntities;
import com.steam.steamcore.registry.ModBlocks;
import com.steam.steamcore.registry.ModCreativeTabs;
import com.steam.steamcore.registry.ModItems;
import com.steam.steamcore.registry.ModMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Mod(SteamCore.MODID)
public class SteamCore {

    public static final String MODID     = "steamcore";
    public static final String PACK_NAME = "SteamCreate 2";

    public static final Logger LOGGER = LogUtils.getLogger();

    public SteamCore(IEventBus modEventBus, ModContainer modContainer) {

        // Deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        com.steam.steamcore.registry.ModSounds.SOUND_EVENTS.register(modEventBus);
        com.steam.steamcore.registry.ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        // Creative tab injection
        modEventBus.addListener(ModCreativeTabs::addCreative);
        modEventBus.addListener(com.steam.steamcore.block.KineticGeneratorBlockEntity::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "steamcore/steamcore.toml");

        ensurePackVersionFile();
    }

    public static String getPackName() {
        return PACK_NAME;
    }

    // Gets the active pack version strictly from config/steamcore/pack_version.txt.

    public static String getPackVersion() {
        Path versionFile = FMLPaths.CONFIGDIR.get().resolve(MODID).resolve("pack_version.txt");
        if (Files.exists(versionFile)) {
            try {
                for (String line : Files.readAllLines(versionFile, StandardCharsets.UTF_8)) {
                    line = line.trim();
                    if (line.startsWith("pack_version=")) {
                        String val = line.substring("pack_version=".length()).trim();
                        if (!val.isEmpty()) return val;
                    }
                }
            } catch (Exception ignored) {}
        }

        return "2.1.5b";
    }


     // Creates initial config/steamcore/pack_version.txt if it does NOT exist.
     //Will NOT overwrite an existing pack_version.txt file.

    private static void ensurePackVersionFile() {
        try {
            Path dir = FMLPaths.CONFIGDIR.get().resolve(MODID);
            Files.createDirectories(dir);

            Path versionFile = dir.resolve("pack_version.txt");
            if (!Files.exists(versionFile)) {
                String content = "# SteamCreate 2 — modpack version & info configuration\n"
                        + "# Edit pack_version below to change your modpack version\n"
                        + "pack_name=" + PACK_NAME + "\n"
                        + "pack_version=2.1.5b\n"
                        + "mod_id=" + MODID + "\n"
                        + "github=https://github.com/Dseelis/Steamcreate2\n"
                        + "curseforge=https://www.curseforge.com/minecraft/modpacks/steamcreate2\n";

                Files.writeString(versionFile, content, StandardCharsets.UTF_8);
                LOGGER.info("[SteamCore] Created initial pack_version.txt: {}", versionFile);
            }
        } catch (IOException e) {
            LOGGER.warn("[SteamCore] Could not create pack_version.txt: {}", e.getMessage());
        }
    }
}
