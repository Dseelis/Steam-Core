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

        // Slot 0: helmet or chestplate input
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 0, 22, 35));

        // Slot 1: Goggles input
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 1, 56, 35));

        // Slot 2: Spyglass input
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 2, 90, 35));

        // Slot 3: Output slot
        this.addSlot(new SlotItemHandler(blockEntity.inventory, 3, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Output slot, cannot insert directly
            }
        });

        layoutPlayerInventorySlots(playerInventory, 8, 104);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            // Attach goggles
            attachGoggles(player);
            return true;
        } else if (id == 1) {
            // Detach goggles
            detachGoggles();
            return true;
        }
        return false;
    }

    private void attachGoggles(Player player) {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack goggles = blockEntity.inventory.getStackInSlot(1);
        ItemStack spyglass = blockEntity.inventory.getStackInSlot(2);
        ItemStack output = blockEntity.inventory.getStackInSlot(3);

        if (isChestplate(helmet)) {
            attachChestplateUpgrades(helmet, goggles, spyglass, output);
            return;
        }

        if (helmet.isEmpty() || (goggles.isEmpty() && spyglass.isEmpty()) || !output.isEmpty()) {
            return;
        }

        if (!goggles.isEmpty()) {
            if (!isItem(goggles, "create", "goggles")) {
                return;
            }
        }

        if ((!goggles.isEmpty() && helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false))
                || (!spyglass.isEmpty() && helmet.getOrDefault(ModDataComponents.HAS_SPYGLASS.get(), false))) {
            return;
        }

        // KubeJS event
        com.steam.steamcore.integration.kubejs.EngineeringTableEvent event =
            com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents.fireEngineeringTableEvent(
                blockEntity.getLevel(), helmet.copy(), goggles.copy(), spyglass.copy(),
                com.steam.steamcore.integration.kubejs.EngineeringTableEvent.Type.ATTACH
            );

        if (event.isCancelled()) {
            String reason = event.getCancelMessage();
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    reason == null || reason.isBlank() ? "Установка отменена скриптом KubeJS" : reason), true);
            return;
        }

        ItemStack result = helmet.copy();
        if (!goggles.isEmpty()) {
            result.set(ModDataComponents.HAS_GOGGLES.get(), true);
        }
        if (!spyglass.isEmpty()) {
            result.set(ModDataComponents.HAS_SPYGLASS.get(), true);
        }

        blockEntity.inventory.setStackInSlot(3, result);

        blockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(1, ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(2, ItemStack.EMPTY);

        blockEntity.setChanged();
    }

    private void attachChestplateUpgrades(ItemStack chestplate, ItemStack ironSheet, ItemStack steel, ItemStack output) {
        boolean addArmor = isItem(ironSheet, "create", "iron_sheet");
        boolean addToughness = isItem(steel, "tfmg", "steel_ingot");
        if (!isChestplate(chestplate) || (!addArmor && !addToughness) || !output.isEmpty()) return;
        if ((!ironSheet.isEmpty() && !addArmor) || (!steel.isEmpty() && !addToughness)
                || (addArmor && chestplate.getOrDefault(ModDataComponents.PRESSED_IRON_ARMOR.get(), false))
                || (addToughness && chestplate.getOrDefault(ModDataComponents.STEEL_TOUGHNESS.get(), false))) return;

        ItemStack result = chestplate.copy();
        if (addArmor) result.set(ModDataComponents.PRESSED_IRON_ARMOR.get(), true);
        if (addToughness) result.set(ModDataComponents.STEEL_TOUGHNESS.get(), true);
        blockEntity.inventory.setStackInSlot(3, result);
        blockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(1, ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(2, ItemStack.EMPTY);
        blockEntity.setChanged();
    }

    private static boolean isChestplate(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof net.minecraft.world.item.ArmorItem armor
                && armor.getEquipmentSlot() == net.minecraft.world.entity.EquipmentSlot.CHEST;
    }

    private static boolean isItem(ItemStack stack, String namespace, String path) {
        return !stack.isEmpty() && net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem())
                .equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private void detachGoggles() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack output = blockEntity.inventory.getStackInSlot(3);

        if (isChestplate(helmet)) {
            detachChestplateUpgrades(helmet, output);
            return;
        }

        if (helmet.isEmpty() || !output.isEmpty()) {
            return;
        }

        boolean hasGoggles = helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false);
        boolean hasSpyglass = helmet.getOrDefault(ModDataComponents.HAS_SPYGLASS.get(), false);
        if (!hasGoggles && !hasSpyglass) {
            return;
        }
        if ((hasGoggles && !blockEntity.inventory.getStackInSlot(1).isEmpty())
                || (hasSpyglass && !blockEntity.inventory.getStackInSlot(2).isEmpty())) return;

        // KubeJS event
        com.steam.steamcore.integration.kubejs.EngineeringTableEvent event =
            com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents.fireEngineeringTableEvent(
                blockEntity.getLevel(), helmet.copy(),
                hasGoggles ? itemStack("create:goggles") : ItemStack.EMPTY,
                hasSpyglass ? itemStack("minecraft:spyglass") : ItemStack.EMPTY,
                com.steam.steamcore.integration.kubejs.EngineeringTableEvent.Type.DETACH
            );

        if (event.isCancelled()) {
            return;
        }

        ItemStack resultHelmet = helmet.copy();
        boolean hadGoggles = resultHelmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false);
        resultHelmet.remove(ModDataComponents.HAS_GOGGLES.get());

        ItemStack goggles = hadGoggles ? itemStack("create:goggles") : ItemStack.EMPTY;
        ItemStack spyglass = hasSpyglass ? itemStack("minecraft:spyglass") : ItemStack.EMPTY;

        resultHelmet.remove(ModDataComponents.HAS_SPYGLASS.get());

        blockEntity.inventory.setStackInSlot(3, resultHelmet);
        blockEntity.inventory.setStackInSlot(1, goggles);
        blockEntity.inventory.setStackInSlot(2, spyglass);

        blockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);

        blockEntity.setChanged();
    }

    private void detachChestplateUpgrades(ItemStack chestplate, ItemStack output) {
        if (!isChestplate(chestplate) || !output.isEmpty()) return;
        boolean hasArmor = chestplate.getOrDefault(ModDataComponents.PRESSED_IRON_ARMOR.get(), false);
        boolean hasToughness = chestplate.getOrDefault(ModDataComponents.STEEL_TOUGHNESS.get(), false);
        if (!hasArmor && !hasToughness) return;
        ItemStack result = chestplate.copy();
        result.remove(ModDataComponents.PRESSED_IRON_ARMOR.get());
        result.remove(ModDataComponents.STEEL_TOUGHNESS.get());
        blockEntity.inventory.setStackInSlot(3, result);
        blockEntity.inventory.setStackInSlot(1, hasArmor ? itemStack("create:iron_sheet") : ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(2, hasToughness ? itemStack("tfmg:steel_ingot") : ItemStack.EMPTY);
        blockEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
        blockEntity.setChanged();
    }

    private static ItemStack itemStack(String id) {
        net.minecraft.resources.ResourceLocation location = net.minecraft.resources.ResourceLocation.parse(id);
        net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(location);
        return item == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    public boolean canAttach() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack goggles = blockEntity.inventory.getStackInSlot(1);
        ItemStack spyglass = blockEntity.inventory.getStackInSlot(2);
        ItemStack output = blockEntity.inventory.getStackInSlot(3);

        if (isChestplate(helmet)) {
            boolean addArmor = isItem(goggles, "create", "iron_sheet");
            boolean addToughness = isItem(spyglass, "tfmg", "steel_ingot");
            return (addArmor || addToughness) && output.isEmpty()
                    && (goggles.isEmpty() || addArmor) && (spyglass.isEmpty() || addToughness)
                    && (!addArmor || !helmet.getOrDefault(ModDataComponents.PRESSED_IRON_ARMOR.get(), false))
                    && (!addToughness || !helmet.getOrDefault(ModDataComponents.STEEL_TOUGHNESS.get(), false));
        }

        if (helmet.isEmpty() || (goggles.isEmpty() && spyglass.isEmpty()) || !output.isEmpty()) {
            return false;
        }

        if ((!goggles.isEmpty() && helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false))
                || (!spyglass.isEmpty() && helmet.getOrDefault(ModDataComponents.HAS_SPYGLASS.get(), false))) {
            return false;
        }

        if (!goggles.isEmpty()) {
            if (!isItem(goggles, "create", "goggles")) return false;
        }
        return spyglass.isEmpty() || spyglass.is(net.minecraft.world.item.Items.SPYGLASS);
    }

    public boolean canDetach() {
        ItemStack helmet = blockEntity.inventory.getStackInSlot(0);
        ItemStack output = blockEntity.inventory.getStackInSlot(3);

        if (isChestplate(helmet)) {
            return output.isEmpty()
                    && (helmet.getOrDefault(ModDataComponents.PRESSED_IRON_ARMOR.get(), false)
                    || helmet.getOrDefault(ModDataComponents.STEEL_TOUGHNESS.get(), false))
                    && blockEntity.inventory.getStackInSlot(1).isEmpty()
                    && blockEntity.inventory.getStackInSlot(2).isEmpty();
        }

        if (helmet.isEmpty() || !output.isEmpty()) {
            return false;
        }

        boolean hasGoggles = helmet.getOrDefault(ModDataComponents.HAS_GOGGLES.get(), false);
        boolean hasSpyglass = helmet.getOrDefault(ModDataComponents.HAS_SPYGLASS.get(), false);
        return (hasGoggles || hasSpyglass)
                && (!hasGoggles || blockEntity.inventory.getStackInSlot(1).isEmpty())
                && (!hasSpyglass || blockEntity.inventory.getStackInSlot(2).isEmpty());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
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
