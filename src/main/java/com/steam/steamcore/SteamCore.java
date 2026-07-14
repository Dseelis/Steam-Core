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
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;


@Mod(SteamCore.MODID)
public class SteamCore {

    public static final String MODID        = "steamcore";
    public static final String PACK_NAME    = "SteamCreate 2";
    public static final String PACK_VERSION = "2.1.5b";

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

        // Config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
