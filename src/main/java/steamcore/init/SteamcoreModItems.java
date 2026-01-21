/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package steamcore.init;

import steamcore.item.*;

import steamcore.SteamcoreMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class SteamcoreModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(SteamcoreMod.MODID);
	public static final DeferredItem<Item> ETERNAL_CORE;
	public static final DeferredItem<Item> GAMMA_ORE;
	public static final DeferredItem<Item> ETERNAL_ORE;
	public static final DeferredItem<Item> ETERNAL_GEM;
	public static final DeferredItem<Item> EMPTY_ETERNAL_GEM;
	public static final DeferredItem<Item> GAMMA_BRICK;
	public static final DeferredItem<Item> WRENCH;
	public static final DeferredItem<Item> SWORD_PART;
	public static final DeferredItem<Item> SWORD_PART_2;
	public static final DeferredItem<Item> FINAL_SWORD;
	public static final DeferredItem<Item> UNFINISHEDENGINEERINGPRESS;
	public static final DeferredItem<Item> UNFINISHEDSILICONPRESS;
	public static final DeferredItem<Item> UNFINISHEDLOGICPRESS;
	public static final DeferredItem<Item> UNFINISHEDCALCULATIONPRESS;
	public static final DeferredItem<Item> SEALED_BLOCK;
	static {
		ETERNAL_CORE = REGISTRY.register("eternal_core", EternalCoreItem::new);
		GAMMA_ORE = block(SteamcoreModBlocks.GAMMA_ORE);
		ETERNAL_ORE = block(SteamcoreModBlocks.ETERNAL_ORE);
		ETERNAL_GEM = REGISTRY.register("eternal_gem", EternalGemItem::new);
		EMPTY_ETERNAL_GEM = REGISTRY.register("empty_eternal_gem", EmptyEternalGemItem::new);
		GAMMA_BRICK = block(SteamcoreModBlocks.GAMMA_BRICK, new Item.Properties().fireResistant());
		WRENCH = REGISTRY.register("wrench", WrenchItem::new);
		SWORD_PART = REGISTRY.register("sword_part", SwordPartItem::new);
		SWORD_PART_2 = REGISTRY.register("sword_part_2", SwordPart2Item::new);
		FINAL_SWORD = REGISTRY.register("final_sword", FinalSwordItem::new);
		UNFINISHEDENGINEERINGPRESS = REGISTRY.register("unfinishedengineeringpress", UnfinishedengineeringpressItem::new);
		UNFINISHEDSILICONPRESS = REGISTRY.register("unfinishedsiliconpress", UnfinishedsiliconpressItem::new);
		UNFINISHEDLOGICPRESS = REGISTRY.register("unfinishedlogicpress", UnfinishedlogicpressItem::new);
		UNFINISHEDCALCULATIONPRESS = REGISTRY.register("unfinishedcalculationpress", UnfinishedcalculationpressItem::new);
		SEALED_BLOCK = block(SteamcoreModBlocks.SEALED_BLOCK, new Item.Properties().stacksTo(1).fireResistant());
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}