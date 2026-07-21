package com.steam.steamcore.integration.kubejs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// Event fired when goggles are being attached or detached in the Engineering Table.
// Can be used by KubeJS to add custom logic or restrictions.
public class EngineeringTableEvent {
    private final Level level;
    private final ItemStack helmet;
    private final ItemStack goggles;
    private final Type type;
    private boolean cancelled = false;
    private String cancelMessage = "";

    public EngineeringTableEvent(Level level, ItemStack helmet, ItemStack goggles, Type type) {
        this.level = level;
        this.helmet = helmet;
        this.goggles = goggles;
        this.type = type;
    }

    public enum Type {
        ATTACH,
        DETACH
    }

     // Get the level where the operation is happening
    public Level getLevel() {
        return level;
    }

     // Get the helmet item
    public ItemStack getHelmet() {
        return helmet;
    }

     // Get the goggles item (may be empty for DETACH operations)
    public ItemStack getGoggles() {
        return goggles;
    }

     // Get the type of operation (ATTACH or DETACH)
    public Type getType() {
        return type;
    }

     // Check if this is an attach operation
    public boolean isAttach() {
        return type == Type.ATTACH;
    }

     // Check if this is a detach operation
    public boolean isDetach() {
        return type == Type.DETACH;
    }

     // Check if this event has been cancelled
    public boolean isCancelled() {
        return cancelled;
    }

     // Cancel the operation
    public void cancel() {
        this.cancelled = true;
    }

     // Cancel the operation with a message
    public void cancel(String message) {
        this.cancelled = true;
        this.cancelMessage = message;
    }

     // Get the cancellation message
    public String getCancelMessage() {
        return cancelMessage;
    }
}
