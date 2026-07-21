package com.steam.steamcore.integration.kubejs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// Event fired when an item is disassembled in the Disassembly Table.
// Can be used by KubeJS to add custom disassembly logic.
public class DisassemblyEvent {
    private final Level level;
    private final ItemStack input;
    private boolean cancelled = false;
    private ItemStack[] customOutputs = null;

    public DisassemblyEvent(Level level, ItemStack input) {
        this.level = level;
        this.input = input;
    }

// Get the level where the disassembly is happening
    public Level getLevel() {
        return level;
    }

    // Get the input item being disassembled
    public ItemStack getInput() {
        return input;
    }


    // Check if this event has been cancelled
    public boolean isCancelled() {
        return cancelled;
    }

     // Cancel the default disassembly logic
    public void cancel() {
        this.cancelled = true;
    }

    // Set custom outputs for this disassembly
    public void setCustomOutputs(ItemStack... outputs) {
        this.customOutputs = outputs;
        this.cancelled = true; // Custom outputs override default behavior
    }

     //Get custom outputs if any were set

    public ItemStack[] getCustomOutputs() {
        return customOutputs;
    }

     //Check if custom outputs were set
    public boolean hasCustomOutputs() {
        return customOutputs != null && customOutputs.length > 0;
    }
}
