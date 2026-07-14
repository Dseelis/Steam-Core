package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SteamCore.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HAS_GOGGLES =
            DATA_COMPONENT_TYPES.register("has_goggles",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(com.mojang.serialization.Codec.BOOL)
                            .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                            .build()
            );
}
