package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SteamCore.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> DISASSEMBLE =
            SOUND_EVENTS.register("disassemble", 
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "disassemble")));
}
