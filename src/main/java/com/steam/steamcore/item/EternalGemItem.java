package com.steam.steamcore.item;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.Config;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EternalGemItem extends Item {

    private static final int MAX_CHARGES    = 5;
    private static final int COOLDOWN_TICKS = 600; // 30 seconds
    private static final int REGEN_DURATION = 200; // 10 seconds
    private static final int RESIST_DURATION= 200;

    public EternalGemItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(MAX_CHARGES)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.pass(stack);

        if (serverPlayer.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        // Apply effects
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_DURATION, 1));
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, RESIST_DURATION, 0));

        // Sound
        serverPlayer.level().playSound(
                null,
                serverPlayer.blockPosition(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                0.8f, 1.4f
        );

        // Particles
        if (serverPlayer.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    serverPlayer.getX(),
                    serverPlayer.getY() + 1,
                    serverPlayer.getZ(),
                    30, 0.3, 0.5, 0.3, 0.05
            );
        }

        serverPlayer.sendSystemMessage(
                Component.literal("The gem pulses with ancient energy...")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );

        // Consume charge
        stack.hurtAndBreak(1, serverPlayer,
                hand == InteractionHand.MAIN_HAND
                        ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                        : net.minecraft.world.entity.EquipmentSlot.OFFHAND
        );

        serverPlayer.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        int chargesLeft = MAX_CHARGES - stack.getDamageValue();

        tooltip.add(Component.literal("A gem pulsing with the energy of a forgotten age.")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Use: ")
                .withStyle(ChatFormatting.WHITE)
                .append(Component.literal("Regeneration II + Resistance I (10s)")
                        .withStyle(ChatFormatting.AQUA)));
        tooltip.add(Component.literal("Charges: ")
                .withStyle(ChatFormatting.WHITE)
                .append(Component.literal(chargesLeft + " / " + MAX_CHARGES)
                        .withStyle(chargesLeft > 0 ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_RED)));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }
}
