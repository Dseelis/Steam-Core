package com.steam.steamcore.registry;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SteamCore.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STEAM_TAB =
            CREATIVE_MODE_TABS.register("steam_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.steamcore"))
                            .icon(() -> new ItemStack(ModItems.ETERNAL_CORE.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.GAMMA_BRICK_ITEM.get());
                                output.accept(ModItems.GAMMA_ORE_ITEM.get());
                                output.accept(ModItems.SEALED_BLOCK_ITEM.get());
                                output.accept(ModItems.ETERNAL_INFUSER_ITEM.get());
                                output.accept(ModItems.ETERNAL_ORE_ITEM.get());
                                output.accept(ModItems.UNFINISHED_CALCULATION_PRESS.get());
                                output.accept(ModItems.UNFINISHED_ENGINEERING_PRESS.get());
                                output.accept(ModItems.UNFINISHED_LOGIC_PRESS.get());
                                output.accept(ModItems.UNFINISHED_SILICON_PRESS.get());
                                output.accept(ModItems.WRENCH.get());
                                output.accept(ModItems.EMPTY_ETERNAL_GEM.get());
                                output.accept(ModItems.ETERNAL_CORE.get());
                                output.accept(ModItems.ETERNAL_GEM.get());
                                output.accept(ModItems.KINETIC_GENERATOR_ITEM.get());

                                if (Config.ENABLE_WIP_ITEMS.get()) {
                                    output.accept(ModItems.FORGOTTEN_ESSENCE.get());
                                    output.accept(ModItems.DISASSEMBLY_TABLE_ITEM.get());
                                    output.accept(ModItems.ENGINEERING_TABLE_ITEM.get());
                                    output.accept(ModItems.ANCIENT_CORE_PLATE.get());
                                    output.accept(ModItems.MECHANICAL_CORE_PLATE.get());
                                    output.accept(ModItems.VOID_CORE_PLATE.get());
                                    output.accept(ModItems.ENDER_CORE_PLATE.get());
                                    output.accept(ModItems.GAMMA_IGNITE.get());
                                    output.accept(ModItems.SWORD_PART.get());
                                    output.accept(ModItems.SWORD_PART_2.get());
                                    output.accept(ModItems.FINAL_SWORD.get());
                                }
                            })
                            .build()
            );

    // Called from SteamCore constructor via modEventBus.addListener
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.SEALED_BLOCK_ITEM);
        }
    }
}
