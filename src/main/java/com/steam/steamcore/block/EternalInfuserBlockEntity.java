package com.steam.steamcore.block;

import com.steam.steamcore.SteamCore;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EternalInfuserBlockEntity extends BlockEntity implements IEnergyStorage {

    public static final int FE_COST    = 1_000;
    public static final int MAX_ENERGY = 10_000;
    public static final int MAX_RECEIVE = 1_000;


    private int energy = 0;

    // ---------- constructor ----------

    public EternalInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(SteamCore.ETERNAL_INFUSER_BE_TYPE.get(), pos, state);
    }

    public InteractionResult tryCharge(Player player, ItemStack held,
                                       Level level, BlockPos pos) {
        if (energy < FE_COST) {
            int deficit = FE_COST - energy;
            player.sendSystemMessage(
                    Component.literal("Not enough energy! Need ")
                            .withStyle(ChatFormatting.RED)
                            .append(Component.literal(deficit + " more FE.")
                                    .withStyle(ChatFormatting.YELLOW))
            );
            return InteractionResult.FAIL;
        }

        energy -= FE_COST;
        setChanged();

        held.shrink(1);

        ItemStack result = new ItemStack(SteamCore.ETERNAL_GEM.get());
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }

        // Звук
        level.playSound(
                null,
                pos,
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.BLOCKS,
                0.7f,
                1.5f
        );

        player.sendSystemMessage(
                Component.literal("Eternal Gem charged! (")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.literal("-" + FE_COST + " FE")
                                .withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal(")").withStyle(ChatFormatting.GREEN))
        );

        return InteractionResult.SUCCESS;
    }

    // ---------- Debug ----------

    public void setEnergy(int amount) {
        this.energy = Math.max(0, Math.min(amount, MAX_ENERGY));
        setChanged();
    }


    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) return 0;

        int accepted = Math.min(maxReceive, Math.min(MAX_RECEIVE, MAX_ENERGY - energy));
        if (!simulate) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        // Инфьюзер не отдаёт энергию наружу
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_ENERGY;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = Math.min(tag.getInt("Energy"), MAX_ENERGY);
    }
}