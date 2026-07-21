package com.steam.steamcore.integration.kubejs;

import com.steam.steamcore.SteamCore;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Manager for KubeJS integration events.
// Handles event firing and registration for custom disassembly and engineering table logic.
public class SteamCoreKubeJSEvents {

    private static final List<Consumer<DisassemblyEvent>> disassemblyListeners = new ArrayList<>();
    private static final List<Consumer<EngineeringTableEvent>> engineeringListeners = new ArrayList<>();
    private static boolean kubeJSLoaded = false;

    static {
        // Check if KubeJS is loaded
        kubeJSLoaded = ModList.get().isLoaded("kubejs");
    }

     // Fire a disassembly event.
     // Returns the event after processing by all listeners.
    public static DisassemblyEvent fireDisassemblyEvent(Level level, ItemStack input) {
        DisassemblyEvent event = new DisassemblyEvent(level, input);

        // Fire to custom listeners (for KubeJS)
        for (Consumer<DisassemblyEvent> listener : disassemblyListeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                SteamCore.LOGGER.error("Error in disassembly event listener", e);
            }
        }

        return event;
    }

     // Fire an engineering table event.
     // Returns the event after processing by all listeners.
    public static EngineeringTableEvent fireEngineeringTableEvent(Level level, ItemStack helmet, ItemStack goggles, EngineeringTableEvent.Type type) {
        EngineeringTableEvent event = new EngineeringTableEvent(level, helmet, goggles, type);

        // Fire to custom listeners (for KubeJS)
        for (Consumer<EngineeringTableEvent> listener : engineeringListeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                SteamCore.LOGGER.error("Error in engineering table event listener", e);
            }
        }

        return event;
    }

     // Register a disassembly event listener (for KubeJS)
    public static void registerDisassemblyListener(Consumer<DisassemblyEvent> listener) {
        disassemblyListeners.add(listener);
    }

     // Register an engineering table event listener (for KubeJS)
    public static void registerEngineeringListener(Consumer<EngineeringTableEvent> listener) {
        engineeringListeners.add(listener);
    }

     // Check if KubeJS is loaded
    public static boolean isKubeJSLoaded() {
        return kubeJSLoaded;
    }

     // Clear all listeners (for testing or reload)
    public static void clearListeners() {
        disassemblyListeners.clear();
        engineeringListeners.clear();
    }
}
