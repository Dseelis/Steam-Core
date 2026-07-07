package com.steam.steamcore.item;

import com.steam.steamcore.block.DisassemblyTableBlock;
import com.steam.steamcore.block.EternalInfuserBlock;
import com.steam.steamcore.block.KineticGeneratorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return new ItemStack(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            boolean isModMachine = block instanceof DisassemblyTableBlock 
                    || block instanceof EternalInfuserBlock 
                    || block instanceof KineticGeneratorBlock;

            if (isModMachine) {
                if (!level.isClientSide) {
                    BlockEntity be = level.getBlockEntity(pos);
                    ItemStack dropStack = new ItemStack(block.asItem());

                    if (be != null) {
                        CompoundTag tag = be.saveWithFullMetadata(level.registryAccess());
                        // Set the block entity data component so it restores when placed down
                        dropStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
                    }

                    // Remove BlockEntity first to prevent double drops from block destruction if any container drop system is implemented
                    level.removeBlockEntity(pos);
                    level.destroyBlock(pos, false, player);

                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);

                    level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 1.5F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }
}
