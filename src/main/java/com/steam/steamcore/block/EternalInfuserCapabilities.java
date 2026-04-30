package com.steam.steamcore.block;

import com.steam.steamcore.registry.ModBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class EternalInfuserCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ETERNAL_INFUSER_BE_TYPE.get(),
                (be, side) -> be
        );
    }
}
