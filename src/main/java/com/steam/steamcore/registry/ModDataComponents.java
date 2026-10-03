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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HAS_SPYGLASS =
            DATA_COMPONENT_TYPES.register("has_spyglass", () -> DataComponentType.<Boolean>builder()
                    .persistent(com.mojang.serialization.Codec.BOOL)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PRESSED_IRON_ARMOR =
            DATA_COMPONENT_TYPES.register("pressed_iron_armor", () -> DataComponentType.<Boolean>builder()
                    .persistent(com.mojang.serialization.Codec.BOOL)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> STEEL_TOUGHNESS =
            DATA_COMPONENT_TYPES.register("steel_toughness", () -> DataComponentType.<Boolean>builder()
                    .persistent(com.mojang.serialization.Codec.BOOL)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.BOOL).build());
}
