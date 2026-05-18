package com.steam.steamcore.inventory;

import com.steam.steamcore.block.DisassemblyTableBlockEntity;
import com.steam.steamcore.registry.ModBlocks;
import com.steam.steamcore.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DisassemblyTableMenu extends AbstractContainerMenu {
    public final DisassemblyTableBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    // Client constructor
    public DisassemblyTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Server constructor
    public DisassemblyTableMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.DISASSEMBLY_TABLE_MENU.get(), containerId);
        this.blockEntity = (DisassemblyTableBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // Input slot (aligned with screen: leftPos + 120, topPos + 20)
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 0, 120, 20));

        // Output slots (aligned with screen: 2x3 grid)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(blockEntity.inventory, 1 + i * 3 + j, 102 + j * 18, 45 + i * 18));
            }
        }

        layoutPlayerInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            blockEntity.disassemble();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 7) {
                if (!this.moveItemStackTo(itemstack1, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, ModBlocks.DISASSEMBLY_TABLE.get());
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory, int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, x + i * 18, y + 58));
        }
    }
}
