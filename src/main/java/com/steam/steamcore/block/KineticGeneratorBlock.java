package com.steam.steamcore.block;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.steam.steamcore.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class KineticGeneratorBlock extends KineticBlock implements IBE<KineticGeneratorBlockEntity> {

    public KineticGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public Class<KineticGeneratorBlockEntity> getBlockEntityClass() {
        return KineticGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticGeneratorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.KINETIC_GENERATOR_BE_TYPE.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.UP || face == Direction.DOWN;
    }
}
