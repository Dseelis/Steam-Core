package com.steam.steamcore.block;

import com.steam.steamcore.Config;
import com.steam.steamcore.registry.ModBlockEntities;
import com.steam.steamcore.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

public class DisassemblyTableBlockEntity extends BlockEntity {

    private static final TagKey<Item> DISASSEMBLES_TO_ESSENCE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("steamcore", "disassembles_to_essence"));

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

        // Check if item is in the disassembles_to_essence tag
        if (input.is(DISASSEMBLES_TO_ESSENCE)) {
            int amount = 1 + level.random.nextInt(3); // 1-3 essence
            addOutput(new ItemStack(ModItems.FORGOTTEN_ESSENCE.get(), amount));
            input.shrink(1);
            return;
        }

        RecipeManager rm = level.getRecipeManager();
        Optional<RecipeHolder<CraftingRecipe>> recipe = rm.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(r -> {
                    ItemStack result = r.value().getResultItem(level.registryAccess());
                    return result.is(input.getItem()) && input.getCount() >= result.getCount();
                })
                .findFirst();

        if (recipe.isPresent()) {
            CraftingRecipe craftingRecipe = recipe.get().value();
            int resultCount = craftingRecipe.getResultItem(level.registryAccess()).getCount();

            for (Ingredient ingredient : craftingRecipe.getIngredients()) {
                if (ingredient.isEmpty()) continue;
                
                ItemStack component = ingredient.getItems()[0].copy();
                
                // chance to recover each item (configurable or random)
                float failChance = Config.DISASSEMBLY_FAIL_CHANCE.get().floatValue();

                if (level.random.nextFloat() > failChance) {
                    addOutput(component);
                }
            }
            
            input.shrink(resultCount);
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
