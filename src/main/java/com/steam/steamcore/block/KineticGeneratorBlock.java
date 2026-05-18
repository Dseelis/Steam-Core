package com.steam.steamcore.block;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import com.steam.steamcore.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KineticGeneratorBlock
        extends KineticBlock
        implements IBE<KineticGeneratorBlockEntity> {

    public KineticGeneratorBlock(Properties properties) {
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
    public boolean hasShaftTowards(LevelReader world,
                                   BlockPos pos,
                                   BlockState state,
                                   Direction face) {

        return face == Direction.UP;
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               CollisionContext context) {

        return box(1, 0, 1, 15, 14, 15);
    }
}