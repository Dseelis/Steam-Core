package com.steam.steamcore.loot;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public class LootTableHandler {

    private static final ResourceLocation NETHER_BRIDGE_CHEST =
            ResourceLocation.withDefaultNamespace("chests/nether_bridge");

    // Cataclysm Boss Loot Tables
    private static final ResourceLocation MONSTROSITY = ResourceLocation.fromNamespaceAndPath("cataclysm", "entities/netherite_monstrosity");
    private static final ResourceLocation IGNIS = ResourceLocation.fromNamespaceAndPath("cataclysm", "entities/ignis");
    private static final ResourceLocation HARBINGER = ResourceLocation.fromNamespaceAndPath("cataclysm", "entities/the_harbinger");
    private static final ResourceLocation ENDER_GUARDIAN = ResourceLocation.fromNamespaceAndPath("cataclysm", "entities/ender_guardian");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();

        if (name.equals(NETHER_BRIDGE_CHEST)) {
            // chance 30%
            LootPool pool = LootPool.lootPool()
                    .name("steamcore_gamma_ignite")
                    .setRolls(ConstantValue.exactly(1))
                    .add(
                            LootItem.lootTableItem(ModItems.GAMMA_IGNITE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                    )
                    .when(LootItemRandomChanceCondition.randomChance(0.3f))
                    .build();

            event.getTable().addPool(pool);
        }

        // --- Cataclysm Bosses ---
        
        if (name.equals(MONSTROSITY)) {
            addBossDrop(event, ModItems.ANCIENT_CORE_PLATE.get());
        } else if (name.equals(IGNIS)) {
            addBossDrop(event, ModItems.VOID_CORE_PLATE.get());
        } else if (name.equals(HARBINGER)) {
            addBossDrop(event, ModItems.MECHANICAL_CORE_PLATE.get());
        } else if (name.equals(ENDER_GUARDIAN)) {
            addBossDrop(event, ModItems.ENDER_CORE_PLATE.get());
        }
    }

    private static void addBossDrop(LootTableLoadEvent event, net.minecraft.world.item.Item item) {
        LootPool pool = LootPool.lootPool()
                .name("steamcore_boss_drop")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item))
                .build(); // Guaranteed drop (100%) for boss progression
        event.getTable().addPool(pool);
    }
}
