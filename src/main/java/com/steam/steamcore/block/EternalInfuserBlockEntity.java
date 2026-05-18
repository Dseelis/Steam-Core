package com.steam.steamcore.block;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.registry.ModBlockEntities;
import com.steam.steamcore.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

@EventBusSubscriber(modid = SteamCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EternalInfuserBlockEntity extends BlockEntity {

    public static final int FE_COST     = 1_000;
    public static final int MAX_ENERGY  = 50_000;
    public static final int MAX_RECEIVE = 10_000;

    protected final ContainerData data;

    public final ModEnergyStorage energyStorage;

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return stack.is(ModItems.EMPTY_ETERNAL_GEM.get());
            return false;
        }
    };

    public EternalInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ETERNAL_INFUSER_BE_TYPE.get(), pos, state);
        
        this.energyStorage = new ModEnergyStorage(MAX_ENERGY, MAX_RECEIVE);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> EternalInfuserBlockEntity.this.energyStorage.getEnergyStored() & 0xFFFF;
                    case 1 -> (EternalInfuserBlockEntity.this.energyStorage.getEnergyStored() >> 16) & 0xFFFF;
                    case 2 -> EternalInfuserBlockEntity.this.MAX_ENERGY & 0xFFFF;
                    case 3 -> (EternalInfuserBlockEntity.this.MAX_ENERGY >> 16) & 0xFFFF;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) { }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EternalInfuserBlockEntity blockEntity) {
        if (level.isClientSide) return;

        ItemStack input = blockEntity.inventory.getStackInSlot(0);
        if (!input.isEmpty()) {
            if (blockEntity.energyStorage.getEnergyStored() >= FE_COST) {
                ItemStack output = blockEntity.inventory.getStackInSlot(1);
                ItemStack result = new ItemStack(ModItems.ETERNAL_GEM.get());

                if (output.isEmpty() || (ItemStack.isSameItemSameComponents(output, result) && output.getCount() < output.getMaxStackSize())) {
                    // Actual extraction
                    if (blockEntity.energyStorage.extractEnergyInternal(FE_COST, false) == FE_COST) {
                        input.shrink(1);
                        if (output.isEmpty()) {
                            blockEntity.inventory.setStackInSlot(1, result);
                        } else {
                            output.grow(1);
                        }
                        blockEntity.setChanged();
                    }
                }
            }
        }
    }

    public int getEnergyStored() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergyStored() { return MAX_ENERGY; }

    public void setEnergy(int amount) {
        this.energyStorage.setEnergyDirectly(amount);
        setChanged();
    }

    public ContainerData getContainerData() {
        return this.data;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.energyStorage.setEnergyDirectly(tag.getInt("Energy"));
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ETERNAL_INFUSER_BE_TYPE.get(),
                (be, side) -> be.energyStorage
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ETERNAL_INFUSER_BE_TYPE.get(),
                (be, side) -> be.inventory
        );
    }

    public class ModEnergyStorage extends EnergyStorage {
        public ModEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, capacity);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) setChanged();
            return received;
        }

        public int extractEnergyInternal(int maxExtract, boolean simulate) {
            int extracted = Math.min(energy, maxExtract);
            if (!simulate) {
                energy -= extracted;
                setChanged();
            }
            return extracted;
        }

        public void setEnergyDirectly(int amount) {
            this.energy = Math.max(0, Math.min(amount, capacity));
            setChanged();
        }
        
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }
    }
}
