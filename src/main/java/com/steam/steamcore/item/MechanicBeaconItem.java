package com.steam.steamcore.item;

import com.steam.steamcore.story.StoryService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class MechanicBeaconItem extends Item {
    public MechanicBeaconItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer && !level.isClientSide) {
            if (!com.steam.steamcore.Config.ENABLE_STORY.get() || !com.steam.steamcore.Config.ENABLE_CLOUDS_SPAWN.get()) {
                serverPlayer.displayClientMessage(Component.translatable("message.steamcore.story_disabled"), true);
                return InteractionResultHolder.fail(stack);
            }
            if (StoryService.returnToClouds(serverPlayer)) {
                serverPlayer.displayClientMessage(Component.translatable("message.steamcore.mechanic_beacon"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
