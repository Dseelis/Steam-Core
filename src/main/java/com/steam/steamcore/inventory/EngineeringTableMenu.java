package com.steam.steamcore.inventory;

import com.steam.steamcore.block.EngineeringTableBlockEntity;
import com.steam.steamcore.registry.ModBlocks;
import com.steam.steamcore.registry.ModDataComponents;
import com.steam.steamcore.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class EngineeringTableMenu extends AbstractContainerMenu {
    public final EngineeringTableBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public EngineeringTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EngineeringTableMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.ENGINEERING_TABLE_MENU.get(), containerId);
        this.blockEntity = (EngineeringTableBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // Slot 0: Helmet input
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 0, 30, 35));

        // Slot 1: Goggles input
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 1, 80, 35));

        // Slot 2: Output slot
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 2, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Output slot, cannot insert directly
            }
        });

        layoutPlayerInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            // Attach goggles
            attachGoggles();
            return true;
        } else if (id == 1) {
            // Detach goggles
            detachGoggles();
            return true;
        }
        return false;
    }

    private void attachGoggles() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack goggles = blockEntity.inventory.getStackInSlot(1);
        ItemStack output = blockEntity.inventory.getStackInSlot(2);

        if (helmet.isEmpty() || goggles.isEmpty() || !output.isEmpty()) {
            return;
        }

        try {
            Class<?> gogglesClass = Class.forName("com.simibubi.create.content.equipment.goggles.GogglesItem");
            if (!gogglesClass.isInstance(goggles.getItem())) {
                return;
            }
        } catch (ClassNotFoundException e) {
            return;
        }

        if (helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false)) {
            return;
        }

        // KubeJS event
        com.steam.steamcore.integration.kubejs.EngineeringTableEvent event =
            com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents.fireEngineeringTableEvent(
                blockEntity.getLevel(), helmet.copy(), goggles.copy(),
                com.steam.steamcore.integration.kubejs.EngineeringTableEvent.Type.ATTACH
            );

        if (event.isCancelled()) {
            return;
        }

        ItemStack result = helmet.copy();
        result.set(ModDataComponents.HAS_GOGGLES.get(), true);

        // Store the goggles item as NBT for later retrieval
        net.minecraft.nbt.CompoundTag gogglesTag = new net.minecraft.nbt.CompoundTag();
        goggles.save(blockEntity.getLevel().registryAccess(), gogglesTag);
        result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(gogglesTag));

        blockEntity.inventory.setStackInSlot(2, result);

        blockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(1, ItemStack.EMPTY);

        blockEntity.setChanged();
    }

    private void detachGoggles() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack output = blockEntity.inventory.getStackInSlot(2);

        if (helmet.isEmpty() || !output.isEmpty()) {
            return;
        }

        if (!helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false)) {
            return;
        }

        // KubeJS event
        com.steam.steamcore.integration.kubejs.EngineeringTableEvent event =
            com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents.fireEngineeringTableEvent(
                blockEntity.getLevel(), helmet.copy(), ItemStack.EMPTY,
                com.steam.steamcore.integration.kubejs.EngineeringTableEvent.Type.DETACH
            );

        if (event.isCancelled()) {
            return;
        }

        ItemStack resultHelmet = helmet.copy();
        resultHelmet.remove(ModDataComponents.HAS_GOGGLES.get());

        // Try to retrieve the original goggles from NBT
        ItemStack goggles = ItemStack.EMPTY;
        net.minecraft.world.item.component.CustomData customData =
                helmet.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);

        if (customData != null) {
            ItemStack parsedStack = ItemStack.parseOptional(
                    blockEntity.getLevel().registryAccess(),
                    customData.copyTag()
            );
            if (!parsedStack.isEmpty()) {
                goggles = parsedStack;
            }
        }

        if (goggles.isEmpty()) {
            try {
                Class<?> gogglesClass = Class.forName("com.simibubi.create.content.equipment.goggles.GogglesItem");
                net.minecraft.world.item.Item gogglesItem = (net.minecraft.world.item.Item)
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.stream()
                        .filter(item -> gogglesClass.isInstance(item))
                        .findFirst()
                        .orElse(null);

                if (gogglesItem != null) {
                    goggles = new ItemStack(gogglesItem);
                }
            } catch (ClassNotFoundException e) {
                // Could not create goggles
            }
        }

        // Remove the stored goggles data
        resultHelmet.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);

        blockEntity.inventory.setStackInSlot(2, resultHelmet);
        blockEntity.inventory.setStackInSlot(1, goggles);

        blockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);

        blockEntity.setChanged();
    }

    public boolean canAttach() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack goggles = blockEntity.inventory.getStackInSlot(1);
        ItemStack output = blockEntity.inventory.getStackInSlot(2);

        if (helmet.isEmpty() || goggles.isEmpty() || !output.isEmpty()) {
            return false;
        }

        if (helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false)) {
            return false;
        }

        try {
            Class<?> gogglesClass = Class.forName("com.simibubi.create.content.equipment.goggles.GogglesItem");
            return gogglesClass.isInstance(goggles.getItem());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean canDetach() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack output = blockEntity.inventory.getStackInSlot(2);

        if (helmet.isEmpty() || !output.isEmpty()) {
            return false;
        }

        return helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
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
        return stillValid(levelAccess, player, ModBlocks.ENGINEERING_TABLE.get());
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
