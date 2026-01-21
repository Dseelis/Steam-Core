/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package steamcore.init;

import steamcore.block.SealedBlockBlock;
import steamcore.block.GammaOreBlock;
import steamcore.block.GammaBrickBlock;
import steamcore.block.EternalOreBlock;

import steamcore.SteamcoreMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

public class SteamcoreModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(SteamcoreMod.MODID);
	public static final DeferredBlock<Block> GAMMA_ORE;
	public static final DeferredBlock<Block> ETERNAL_ORE;
	public static final DeferredBlock<Block> GAMMA_BRICK;
	public static final DeferredBlock<Block> SEALED_BLOCK;
	static {
		GAMMA_ORE = REGISTRY.register("gamma_ore", GammaOreBlock::new);
		ETERNAL_ORE = REGISTRY.register("eternal_ore", EternalOreBlock::new);
		GAMMA_BRICK = REGISTRY.register("gamma_brick", GammaBrickBlock::new);
		SEALED_BLOCK = REGISTRY.register("sealed_block", SealedBlockBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}