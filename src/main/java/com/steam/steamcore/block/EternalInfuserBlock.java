package com.steam.steamcore.block;

import com.steam.steamcore.SteamCore;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public class EternalInfuserBlock extends BaseEntityBlock {

    public static final MapCodec<EternalInfuserBlock> CODEC =
            simpleCodec(EternalInfuserBlock::new);

    public EternalInfuserBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    // BlockEntity

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EternalInfuserBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,
                                               BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof EternalInfuserBlockEntity infuser)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getMainHandItem();

        if (held.isEmpty()) {
            int stored = infuser.getEnergyStored();
            int max    = infuser.getMaxEnergyStored();
            player.sendSystemMessage(
                    Component.literal("Eternal Infuser: ")
                            .withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(stored + " / " + max + " FE")
                                    .withStyle(ChatFormatting.YELLOW))
            );
            return InteractionResult.SUCCESS;
        }

        if (held.is(SteamCore.EMPTY_ETERNAL_GEM.get())) {
            return infuser.tryCharge(player, held, level, pos);
        }

        return InteractionResult.PASS;
    }
}
