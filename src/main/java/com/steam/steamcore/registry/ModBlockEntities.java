package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.block.EternalInfuserBlockEntity;
import com.steam.steamcore.block.KineticGeneratorBlockEntity;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.steam.steamcore.block.DisassemblyTableBlockEntity>>
            DISASSEMBLY_TABLE_BE_TYPE = BLOCK_ENTITY_TYPES.register(
            "disassembly_table",
            () -> BlockEntityType.Builder
                    .of(com.steam.steamcore.block.DisassemblyTableBlockEntity::new, ModBlocks.DISASSEMBLY_TABLE.get())
                    .build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticGeneratorBlockEntity>>
            KINETIC_GENERATOR_BE_TYPE = BLOCK_ENTITY_TYPES.register(
            "kinetic_generator",
            () -> {
                BlockEntityType<?>[] holder = new BlockEntityType<?>[1];
                BlockEntityType<KineticGeneratorBlockEntity> type = BlockEntityType.Builder
                        .of((pos, state) -> new KineticGeneratorBlockEntity(
                                        (BlockEntityType<KineticGeneratorBlockEntity>) holder[0], pos, state),
                                ModBlocks.KINETIC_GENERATOR.get())
                        .build(null);
                holder[0] = type;
                return type;
            }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.steam.steamcore.block.EngineeringTableBlockEntity>>
            ENGINEERING_TABLE_BE_TYPE = BLOCK_ENTITY_TYPES.register(
            "engineering_table",
            () -> BlockEntityType.Builder
                    .of(com.steam.steamcore.block.EngineeringTableBlockEntity::new, ModBlocks.ENGINEERING_TABLE.get())
                    .build(null)
    );
}
