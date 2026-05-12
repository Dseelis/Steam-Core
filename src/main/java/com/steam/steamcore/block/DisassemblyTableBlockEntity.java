package com.steam.steamcore.block;

import com.steam.steamcore.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DisassemblyTableBlockEntity extends BlockEntity {

    // 1 input slot (0), 6 output slots (1-6)
    public final ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public DisassemblyTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISASSEMBLY_TABLE_BE_TYPE.get(), pos, state);
    }

    public void disassemble() {
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return;

        RecipeManager rm = level.getRecipeManager();
        Optional<RecipeHolder<CraftingRecipe>> recipe = rm.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(r -> r.value().getResultItem(level.registryAccess()).is(input.getItem()))
                .findFirst();

        if (recipe.isPresent()) {
            CraftingRecipe craftingRecipe = recipe.get().value();

            for (Ingredient ingredient : craftingRecipe.getIngredients()) {
                if (ingredient.isEmpty()) continue;
                
                ItemStack component = ingredient.getItems()[0].copy();
                
                // 80% chance to recover each item (configurable or random)
                if (level.random.nextFloat() < 0.8f) {
                    addOutput(component);
                }
            }
            
            input.shrink(1);
        }
    }

    private void addOutput(ItemStack stack) {
        for (int i = 1; i < 7; i++) {
            ItemStack existing = inventory.getStackInSlot(i);
            if (existing.isEmpty()) {
                inventory.setStackInSlot(i, stack);
                return;
            } else if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                int toAdd = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(toAdd);
                stack.shrink(toAdd);
                if (stack.isEmpty()) return;
            }
        }
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
