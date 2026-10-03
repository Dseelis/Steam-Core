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

    // 0 = armor, 1 = goggles or Create iron sheet, 2 = spyglass or TFMG steel, 3 = output
    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 1) {
                return isItem(stack, "create", "iron_sheet") || isItem(stack, "create", "goggles");
            }
            if (slot == 2) {
                return stack.is(net.minecraft.world.item.Items.SPYGLASS) || isItem(stack, "tfmg", "steel_ingot");
            }
            if (slot == 3) {
                // Output slot is not directly insertable
                return false;
            }
            return super.isItemValid(slot, stack);
        }
    };

    private static boolean isItem(ItemStack stack, String namespace, String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem())
                .equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

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
            CompoundTag inventoryTag = tag.getCompound("Inventory");
            if (inventoryTag.getInt("Size") == 3) {
                ItemStackHandler oldInventory = new ItemStackHandler(3);
                oldInventory.deserializeNBT(registries, inventoryTag);
                inventory.setStackInSlot(0, oldInventory.getStackInSlot(0));
                inventory.setStackInSlot(1, oldInventory.getStackInSlot(1));
                inventory.setStackInSlot(3, oldInventory.getStackInSlot(2));
            } else {
                inventory.deserializeNBT(registries, inventoryTag);
            }
        }
    }
}
