package com.steam.steamcore.client.event;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = SteamCore.MODID, value = Dist.CLIENT)
public class ItemTooltipEventHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        boolean hasUpgrades = stack.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false)
                || stack.getOrDefault(ModDataComponents.HAS_SPYGLASS.get(), false)
                || stack.getOrDefault(ModDataComponents.PRESSED_IRON_ARMOR.get(), false)
                || stack.getOrDefault(ModDataComponents.STEEL_TOUGHNESS.get(), false);
        if (hasUpgrades) {
            List<Component> tooltip = event.getToolTip();
            Component upgradesComponent = Component.translatable("tooltip.steamcore.upgrades_equipped")
                    .withStyle(ChatFormatting.GOLD);

            if (tooltip.isEmpty()) {
                tooltip.add(upgradesComponent);
            } else {
                tooltip.add(1, upgradesComponent);
            }
        }
    }
}
