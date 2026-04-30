package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.block.EternalInfuserBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SteamCore.MODID);

    public static final DeferredBlock<Block> GAMMA_BRICK =
            BLOCKS.registerSimpleBlock("gamma_brick",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3f));

    public static final DeferredBlock<Block> GAMMA_ORE =
            BLOCKS.registerSimpleBlock("gamma_ore",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3f));

    public static final DeferredBlock<Block> SEALED_BLOCK =
            BLOCKS.registerSimpleBlock("sealed_block",
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4f));

    public static final DeferredBlock<EternalInfuserBlock> ETERNAL_INFUSER =
            BLOCKS.register("eternal_infuser",
                    () -> new EternalInfuserBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.METAL)
                                    .strength(4f)
                    ));

    public static final DeferredBlock<Block> ETERNAL_ORE =
            BLOCKS.registerSimpleBlock("eternal_ore",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4f));
}
