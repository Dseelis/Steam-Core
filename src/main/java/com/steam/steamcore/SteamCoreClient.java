package com.steam.steamcore;

import com.steam.steamcore.client.gui.ClothConfigScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = SteamCore.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SteamCore.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SteamCoreClient {
    public SteamCoreClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> ClothConfigScreen.create(parent));
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        SteamCore.LOGGER.info("HELLO FROM CLIENT SETUP");
        SteamCore.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(com.steam.steamcore.registry.ModMenuTypes.DISASSEMBLY_TABLE_MENU.get(),
                com.steam.steamcore.client.gui.DisassemblyTableScreen::new);
        event.register(com.steam.steamcore.registry.ModMenuTypes.ETERNAL_INFUSER_MENU.get(),
                com.steam.steamcore.client.gui.EternalInfuserScreen::new);
        event.register(com.steam.steamcore.registry.ModMenuTypes.ENGINEERING_TABLE_MENU.get(),
                com.steam.steamcore.client.gui.EngineeringTableScreen::new);
    }
}
