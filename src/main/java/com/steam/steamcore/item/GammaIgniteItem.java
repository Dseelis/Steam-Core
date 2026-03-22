package com.steam.steamcore.item;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.Config;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = SteamCore.MODID)
public class GammaIgniteItem extends Item {

    private static final Map<UUID, Integer> pendingTeleports = new HashMap<>();

    private static final int TELEPORT_DELAY = 20; // 1 second
    private static final int COOLDOWN = 100;      // 5 second
    private static final int MAX_USES = 3;

    public GammaIgniteItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(MAX_USES)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }


        if (!Config.ENABLE_GAMMA_IGNITE.get()) {

            serverPlayer.sendSystemMessage(
                    Component.literal("Gamma Ignite is disabled in config.")
                            .withStyle(ChatFormatting.RED)
            );

            return InteractionResultHolder.fail(stack);
        }

        if (level.dimension() == Level.OVERWORLD) {
            serverPlayer.sendSystemMessage(
                    Component.literal("Gamma Ignite cannot be used in the Overworld.")
                            .withStyle(ChatFormatting.RED)
            );
            return InteractionResultHolder.fail(stack);
        }

        // If teleportation is already underway
        if (pendingTeleports.containsKey(serverPlayer.getUUID())) {
            return InteractionResultHolder.fail(stack);
        }

        serverPlayer.getCooldowns().addCooldown(this, COOLDOWN);

        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 1));

        serverPlayer.level().playSound(
                null,
                serverPlayer.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.2f,
                0.7f
        );

        ServerLevel serverLevel = serverPlayer.serverLevel();

        serverLevel.sendParticles(
                ParticleTypes.PORTAL,
                serverPlayer.getX(),
                serverPlayer.getY() + 1,
                serverPlayer.getZ(),
                80,
                0.5, 1, 0.5,
                0.2
        );

        serverPlayer.sendSystemMessage(
                Component.literal("Gamma ignition charging...")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );

        pendingTeleports.put(serverPlayer.getUUID(), TELEPORT_DELAY);

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    // Tick handler
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        UUID id = player.getUUID();
        if (!pendingTeleports.containsKey(id)) return;

        int ticksLeft = pendingTeleports.get(id) - 1;

        if (ticksLeft <= 0) {

            ItemStack stack = player.getMainHandItem();

            teleportToRespawn(player);

            stack.hurtAndBreak(1, player,
                    net.minecraft.world.entity.EquipmentSlot.MAINHAND
            );

            pendingTeleports.remove(id);

        } else {
            pendingTeleports.put(id, ticksLeft);
        }
    }


    // TP method
    private static void teleportToRespawn(ServerPlayer player) {

        MinecraftServer server = player.server;
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        if (overworld == null) return;

        BlockPos respawnPos = player.getRespawnPosition();

        BlockPos target = (respawnPos != null)
                ? respawnPos
                : overworld.getSharedSpawnPos();

        player.teleportTo(
                overworld,
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }
    public static void cancelPendingTeleport(UUID uuid) {
        pendingTeleports.remove(uuid);
    }
    // Tooltip
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        int usesLeft = MAX_USES - stack.getDamageValue();

        tooltip.add(Component.literal("Teleports you to your bed spawn.")
                .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal("Uses left: " + usesLeft)
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}