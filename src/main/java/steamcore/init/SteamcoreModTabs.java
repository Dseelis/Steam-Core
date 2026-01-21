/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package steamcore.init;

import steamcore.SteamcoreMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class SteamcoreModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SteamcoreMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = REGISTRY.register("tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.steamcore.tab")).icon(() -> new ItemStack(SteamcoreModItems.ETERNAL_CORE.get())).displayItems((parameters, tabData) -> {
				tabData.accept(SteamcoreModItems.ETERNAL_CORE.get());
				tabData.accept(SteamcoreModBlocks.GAMMA_ORE.get().asItem());
				tabData.accept(SteamcoreModBlocks.ETERNAL_ORE.get().asItem());
				tabData.accept(SteamcoreModItems.ETERNAL_GEM.get());
				tabData.accept(SteamcoreModItems.EMPTY_ETERNAL_GEM.get());
				tabData.accept(SteamcoreModBlocks.GAMMA_BRICK.get().asItem());
				tabData.accept(SteamcoreModItems.WRENCH.get());
				tabData.accept(SteamcoreModItems.SWORD_PART.get());
				tabData.accept(SteamcoreModItems.SWORD_PART_2.get());
				tabData.accept(SteamcoreModItems.FINAL_SWORD.get());
				tabData.accept(SteamcoreModItems.UNFINISHEDENGINEERINGPRESS.get());
				tabData.accept(SteamcoreModItems.UNFINISHEDSILICONPRESS.get());
				tabData.accept(SteamcoreModItems.UNFINISHEDLOGICPRESS.get());
				tabData.accept(SteamcoreModItems.UNFINISHEDCALCULATIONPRESS.get());
				tabData.accept(SteamcoreModBlocks.SEALED_BLOCK.get().asItem());
			}).build());
}