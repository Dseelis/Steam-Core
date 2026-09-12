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

        if (stack.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false)) {
            List<Component> tooltip = event.getToolTip();
            Component gogglesComponent = Component.translatable("tooltip.steamcore.goggles_attached")
                    .withStyle(ChatFormatting.GOLD);

            if (tooltip.isEmpty()) {
                tooltip.add(gogglesComponent);
            } else {
                tooltip.add(1, gogglesComponent);
            }
        }
    }
}
