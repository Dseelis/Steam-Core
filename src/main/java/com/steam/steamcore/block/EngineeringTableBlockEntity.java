package com.steam.steamcore.block;

import com.steam.steamcore.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EngineeringTableBlockEntity extends BlockEntity {

    // 3 slots: 0 = helmet input, 1 = goggles input, 2 = output
    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 1) {
                // Slot 1 only accepts goggles from Create mod
                try {
                    Class<?> gogglesClass = Class.forName("com.simibubi.create.content.equipment.goggles.GogglesItem");
                    return gogglesClass.isInstance(stack.getItem());
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
            if (slot == 2) {
                // Output slot is not directly insertable
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };

    public EngineeringTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENGINEERING_TABLE_BE_TYPE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }
}
