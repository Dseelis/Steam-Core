package com.steam.steamcore.block;

import com.mojang.serialization.MapCodec;
import com.steam.steamcore.inventory.DisassemblyTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DisassemblyTableBlock extends BaseEntityBlock {
    public static final MapCodec<DisassemblyTableBlock> CODEC = simpleCodec(DisassemblyTableBlock::new);

    public DisassemblyTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisassemblyTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DisassemblyTableBlockEntity disassemblyBE) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new DisassemblyTableMenu(id, inv, disassemblyBE),
                        net.minecraft.network.chat.Component.literal("Disassembly Table")
                ), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
