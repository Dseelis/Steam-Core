package com.steam.steamcore.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.steam.steamcore.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class KineticGeneratorBlockEntity extends KineticBlockEntity {

    public static final double CONVERSION_RATE = 0.5;

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(10000, 1000);

    public KineticGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        float speed = Math.abs(getSpeed());
        if (speed > 0) {
            int generated = (int) (speed * CONVERSION_RATE);
            energyStorage.addEnergyInternal(generated);
        }

        if (energyStorage.getEnergyStored() > 0) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = worldPosition.relative(direction);
                IEnergyStorage neighborEnergy = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite());

                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int sent = neighborEnergy.receiveEnergy(
                            Math.min(energyStorage.getEnergyStored(), 500), false);
                    energyStorage.extractEnergyInternal(sent);
                }
            }
        }
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.KINETIC_GENERATOR_BE_TYPE.get(),
                (be, side) -> be.energyStorage
        );
    }

    private static class ModEnergyStorage implements IEnergyStorage {
        private int energy;
        private final int capacity;
        private final int maxExtract;

        public ModEnergyStorage(int capacity, int maxExtract) {
            this.capacity = capacity;
            this.maxExtract = maxExtract;
        }

        public void addEnergyInternal(int amount) {
            this.energy = Math.min(capacity, this.energy + amount);
        }

        public void extractEnergyInternal(int amount) {
            this.energy = Math.max(0, this.energy - amount);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0; // не приймає ззовні
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = Math.min(this.energy, Math.min(maxExtract, this.maxExtract));
            if (!simulate) this.energy -= extracted;
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return capacity;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
