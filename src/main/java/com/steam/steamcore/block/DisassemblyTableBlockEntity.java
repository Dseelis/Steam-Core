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

import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;

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

    public boolean canDisassemble() {
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(input.getItem());
        String namespace = itemId.getNamespace();

        boolean isRelic = input.is(DISASSEMBLES_TO_ESSENCE)
                || namespace.equals("relics")
                || namespace.equals("artifacts")
                || namespace.equals("accessories")
                || namespace.equals("enigmaticlegacy")
                || namespace.contains("relic")
                || namespace.contains("artifact")
                || (namespace.equals("steamcore") && itemId.getPath().endsWith("_core_plate"));

        if (isRelic) {
            return hasRoomFor(new ItemStack(ModItems.FORGOTTEN_ESSENCE.get(), 2));
        }

        RecipeManager rm = level.getRecipeManager();
        return rm.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .anyMatch(r -> {
                    ItemStack result = r.value().getResultItem(level.registryAccess());
                    return result.is(input.getItem()) && input.getCount() >= result.getCount();
                });
    }

    public void disassemble() {
        if (!canDisassemble()) return;

        ItemStack input = inventory.getStackInSlot(0);
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(input.getItem());
        String namespace = itemId.getNamespace();

        if (level != null) {
            if (Config.ENABLE_DISASSEMBLY_SOUND.get()) {
                level.playSound(null, worldPosition, com.steam.steamcore.registry.ModSounds.DISASSEMBLE.get(), 
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        // Check if item is a relic/artifact (tag, mod namespace, or core plates)
        boolean isRelic = input.is(DISASSEMBLES_TO_ESSENCE)
                || namespace.equals("relics")
                || namespace.equals("artifacts")
                || namespace.equals("accessories")
                || namespace.equals("enigmaticlegacy")
                || namespace.contains("relic")
                || namespace.contains("artifact")
                || (namespace.equals("steamcore") && itemId.getPath().endsWith("_core_plate"));

        if (isRelic) {
            // Relics give a more generous amount of essence (2-5)
            int amount = 2 + level.random.nextInt(4);
            ItemStack essence = new ItemStack(ModItems.FORGOTTEN_ESSENCE.get(), amount);

            if (hasRoomFor(essence)) {
                addOutput(essence);
                input.shrink(1);
                setChanged();
            }
            return;
        }

        RecipeManager rm = level.getRecipeManager();
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>(rm.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(r -> {
                    ItemStack result = r.value().getResultItem(level.registryAccess());
                    return result.is(input.getItem()) && input.getCount() >= result.getCount();
                })
                .toList());

        if (recipes.isEmpty()) return;

        // Sort to find the most "canonical" recipe
        String itemNamespace = namespace; // Already calculated

        recipes.sort((a, b) -> {
            // 1. Prefer recipes from the same mod as the item
            boolean aSameMod = a.id().getNamespace().equals(itemNamespace);
            boolean bSameMod = b.id().getNamespace().equals(itemNamespace);
            if (aSameMod != bSameMod) return aSameMod ? -1 : 1;

            // 2. Prefer recipes that consume more of the input (higher result count)
            int aCount = a.value().getResultItem(level.registryAccess()).getCount();
            int bCount = b.value().getResultItem(level.registryAccess()).getCount();
            if (aCount != bCount) return Integer.compare(bCount, aCount);

            // 3. Prefer recipes with more ingredients (avoid simple 1-to-1 conversions)
            int aIng = a.value().getIngredients().size();
            int bIng = b.value().getIngredients().size();
            return Integer.compare(bIng, aIng);
        });

        CraftingRecipe craftingRecipe = recipes.get(0).value();
        int resultCount = craftingRecipe.getResultItem(level.registryAccess()).getCount();

        // Collect potential outputs to check for space
        List<ItemStack> potentialOutputs = new ArrayList<>();
        float failChance = Config.DISASSEMBLY_FAIL_CHANCE.get().floatValue();

        for (Ingredient ingredient : craftingRecipe.getIngredients()) {
            if (ingredient.isEmpty()) continue;

            ItemStack[] items = ingredient.getItems();
            if (items.length == 0) continue;

            if (level.random.nextFloat() > failChance) {
                // Try to find an item from the same mod as the result, or just pick index 0
                ItemStack component = items[0].copy();
                for (ItemStack s : items) {
                    ResourceLocation sId = BuiltInRegistries.ITEM.getKey(s.getItem());
                    
                    // Prevention: don't disassemble into bamboo unless the original item is from a bamboo-related mod
                    if (sId.getPath().equals("bamboo") && !itemNamespace.contains("bamboo")) {
                        continue;
                    }

                    if (sId.getNamespace().equals(itemNamespace)) {
                        component = s.copy();
                        break;
                    }
                }

                // Final check: if we ended up with bamboo and it's not appropriate, skip this ingredient
                ResourceLocation finalId = BuiltInRegistries.ITEM.getKey(component.getItem());
                if (finalId.getPath().equals("bamboo") && !itemNamespace.contains("bamboo")) {
                    continue;
                }

                potentialOutputs.add(component);
            }
        }

        // Only proceed if we can fit all components
        if (hasRoomFor(potentialOutputs)) {
            for (ItemStack output : potentialOutputs) {
                addOutput(output);
            }
            input.shrink(resultCount);
            setChanged();
        }
    }

    private boolean hasRoomFor(ItemStack stack) {
        ItemStack copy = stack.copy();
        for (int i = 1; i < 7; i++) {
            ItemStack existing = inventory.getStackInSlot(i);
            if (existing.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(existing, copy)) {
                int canAdd = existing.getMaxStackSize() - existing.getCount();
                if (canAdd >= copy.getCount()) return true;
                copy.shrink(canAdd);
            }
        }
        return copy.isEmpty();
    }

    private boolean hasRoomFor(List<ItemStack> stacks) {
        // Simple heuristic: if any stack can't fit at all, return false
        // For a more accurate check, we'd need to simulate the entire insertion
        for (ItemStack s : stacks) {
            if (!hasRoomFor(s)) return false;
        }
        return true;
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
