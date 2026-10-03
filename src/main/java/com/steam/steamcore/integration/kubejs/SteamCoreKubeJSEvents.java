package com.steam.steamcore.integration.kubejs;

import com.steam.steamcore.SteamCore;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
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

        dispatchKubeJS("disassembly", event);

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
        return fireEngineeringTableEvent(level, helmet, goggles, ItemStack.EMPTY, type);
    }

    public static EngineeringTableEvent fireEngineeringTableEvent(Level level, ItemStack helmet, ItemStack goggles, ItemStack spyglass, EngineeringTableEvent.Type type) {
        EngineeringTableEvent event = new EngineeringTableEvent(level, helmet, goggles, spyglass, type);

        dispatchKubeJS("engineeringTable", event);

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

    public static MobLevelHookEvent fireMobLevelEvent(Level level, Mob mob, int proposedLevel) {
        MobLevelHookEvent event = new MobLevelHookEvent(level, mob, proposedLevel);
        dispatchKubeJS("mobLevel", event);
        return event;
    }

    public static BossProgressHookEvent fireBossProgressEvent(Level level, LivingEntity boss, String bossId, int points) {
        BossProgressHookEvent event = new BossProgressHookEvent(level, boss, bossId, points);
        dispatchKubeJS("bossProgress", event);
        return event;
    }

    public static void fireDialogueChoiceEvent(net.minecraft.server.level.ServerPlayer player, String dialogueId, String nodeId, String actionId) {
        dispatchKubeJS("dialogueChoice", new DialogueChoiceHookEvent(player, dialogueId, nodeId, actionId));
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

    private static void dispatchKubeJS(String eventName, Object event) {
        if (!kubeJSLoaded) return;
        try {
            Class<?> plugin = Class.forName("com.steam.steamcore.integration.kubejs.SteamCoreKubeJSPlugin");
            plugin.getMethod("post", String.class, Object.class).invoke(null, eventName, event);
        } catch (ReflectiveOperationException e) {
            SteamCore.LOGGER.error("Could not dispatch SteamCore KubeJS event {}", eventName, e);
        }
    }

     // Clear all listeners (for testing or reload)
    public static void clearListeners() {
        disassemblyListeners.clear();
        engineeringListeners.clear();
    }
}
