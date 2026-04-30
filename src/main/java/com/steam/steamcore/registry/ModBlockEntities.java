package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.block.EternalInfuserBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SteamCore.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EternalInfuserBlockEntity>>
            ETERNAL_INFUSER_BE_TYPE = BLOCK_ENTITY_TYPES.register(
            "eternal_infuser",
            () -> BlockEntityType.Builder
                    .of(EternalInfuserBlockEntity::new, ModBlocks.ETERNAL_INFUSER.get())
                    .build(null)
    );
}
