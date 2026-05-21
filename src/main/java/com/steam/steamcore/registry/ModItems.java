package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SteamCore.MODID);

    // Block Items

    public static final DeferredItem<BlockItem> GAMMA_BRICK_ITEM =
            ITEMS.registerSimpleBlockItem("gamma_brick", ModBlocks.GAMMA_BRICK);

    public static final DeferredItem<BlockItem> GAMMA_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("gamma_ore", ModBlocks.GAMMA_ORE);

    public static final DeferredItem<BlockItem> SEALED_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("sealed_block", ModBlocks.SEALED_BLOCK);

    public static final DeferredItem<BlockItem> ETERNAL_INFUSER_ITEM =
            ITEMS.registerSimpleBlockItem("eternal_infuser", ModBlocks.ETERNAL_INFUSER);

    public static final DeferredItem<BlockItem> ETERNAL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("eternal_ore", ModBlocks.ETERNAL_ORE);

    public static final DeferredItem<BlockItem> DISASSEMBLY_TABLE_ITEM =
            ITEMS.registerSimpleBlockItem("disassembly_table", ModBlocks.DISASSEMBLY_TABLE);

    public static final DeferredItem<BlockItem> KINETIC_GENERATOR_ITEM =
            ITEMS.registerSimpleBlockItem("kinetic_generator", ModBlocks.KINETIC_GENERATOR);

    // Items

    public static final DeferredItem<Item> UNFINISHED_CALCULATION_PRESS =
            ITEMS.registerSimpleItem("unfinished_calculation_press");

    public static final DeferredItem<Item> UNFINISHED_ENGINEERING_PRESS =
            ITEMS.registerSimpleItem("unfinished_engineering_press");

    public static final DeferredItem<Item> UNFINISHED_LOGIC_PRESS =
            ITEMS.registerSimpleItem("unfinished_logic_press");

    public static final DeferredItem<Item> UNFINISHED_SILICON_PRESS =
            ITEMS.registerSimpleItem("unfinished_silicon_press");

    public static final DeferredItem<Item> WRENCH =
            ITEMS.register("wrench",
                    () -> new Item(new Item.Properties().stacksTo(1)) {
                        @Override
                        public boolean hasCraftingRemainingItem(ItemStack stack) {
                            return true;
                        }

                        @Override
                        public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
                            return new ItemStack(this);
                        }
                    });

    public static final DeferredItem<Item> FORGOTTEN_ESSENCE =
            ITEMS.registerSimpleItem("forgotten_essence");

    public static final DeferredItem<Item> ANCIENT_CORE_PLATE =
            ITEMS.registerSimpleItem("ancient_core_plate");

    public static final DeferredItem<Item> MECHANICAL_CORE_PLATE =
            ITEMS.registerSimpleItem("mechanical_core_plate");

    public static final DeferredItem<Item> VOID_CORE_PLATE =
            ITEMS.registerSimpleItem("void_core_plate");

    public static final DeferredItem<Item> ENDER_CORE_PLATE =
            ITEMS.registerSimpleItem("ender_core_plate");

    public static final DeferredItem<Item> EMPTY_ETERNAL_GEM =
            ITEMS.registerSimpleItem("empty_eternal_gem");

    public static final DeferredItem<Item> ETERNAL_CORE =
            ITEMS.registerSimpleItem("eternal_core");

    public static final DeferredItem<Item> GAMMA_IGNITE =
            ITEMS.register("gamma_ignite",
                    () -> new com.steam.steamcore.item.GammaIgniteItem(
                            new Item.Properties().stacksTo(1)
                    ));

    public static final DeferredItem<Item> ETERNAL_GEM =
            ITEMS.register("eternal_gem",
                    () -> new com.steam.steamcore.item.EternalGemItem(
                            new Item.Properties().stacksTo(1)
                    ));

    public static final DeferredItem<Item> SWORD_PART =
            ITEMS.registerSimpleItem("sword_part");

    public static final DeferredItem<Item> SWORD_PART_2 =
            ITEMS.registerSimpleItem("sword_part_2");

    public static final DeferredItem<Item> FINAL_SWORD =
            ITEMS.register("final_sword",
                    () -> new SwordItem(
                            Tiers.DIAMOND,
                            new Item.Properties()
                                    .attributes(SwordItem.createAttributes(
                                            Tiers.DIAMOND,
                                            5,
                                            -2.4F
                                    ))
                                    .stacksTo(1)
                    ));
}
