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

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {

        if (!event.getName().equals(NETHER_BRIDGE_CHEST)) return;

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
}
