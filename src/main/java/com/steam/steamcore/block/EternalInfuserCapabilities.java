package com.steam.steamcore.block;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import com.steam.steamcore.SteamCore;

public class EternalInfuserCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,          // capability
                SteamCore.ETERNAL_INFUSER_BE_TYPE.get(),   //  BlockEntity
                (be, side) -> be                           // be сам реализует IEnergyStorage
        );
    }
}
