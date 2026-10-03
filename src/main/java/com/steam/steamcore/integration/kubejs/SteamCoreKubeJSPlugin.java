package com.steam.steamcore.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.item.ItemStack;

// KubeJS-only adapter, loaded only when KubeJS discovers the plugin descriptor.
public final class SteamCoreKubeJSPlugin implements KubeJSPlugin {
    private static final EventGroup GROUP = EventGroup.of("SteamCoreEvents");
    private static final EventHandler DISASSEMBLY = GROUP.server("disassembly", () -> DisassemblyScriptEvent.class);
    private static final EventHandler ENGINEERING_TABLE = GROUP.server("engineeringTable", () -> EngineeringTableScriptEvent.class);

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
    }

    public static void post(String name, Object source) {
        if ("disassembly".equals(name) && source instanceof DisassemblyEvent event) {
            DisassemblyScriptEvent scriptEvent = new DisassemblyScriptEvent(event);
            DISASSEMBLY.post(ScriptType.SERVER, scriptEvent);
            event.setCancelled(scriptEvent.cancelled);
            if (scriptEvent.outputsSet) event.setCustomOutputs(scriptEvent.outputs);
        } else if ("engineeringTable".equals(name) && source instanceof EngineeringTableEvent event) {
            EngineeringTableScriptEvent scriptEvent = new EngineeringTableScriptEvent(event);
            ENGINEERING_TABLE.post(ScriptType.SERVER, scriptEvent);
            if (scriptEvent.cancelled) event.cancel(scriptEvent.cancelMessage);
        }
    }

    public static final class DisassemblyScriptEvent implements KubeEvent {
        private final DisassemblyEvent source;
        private boolean cancelled;
        private boolean outputsSet;
        private ItemStack[] outputs;

        private DisassemblyScriptEvent(DisassemblyEvent source) {
            this.source = source;
        }

        public net.minecraft.world.level.Level getLevel() { return source.getLevel(); }
        public ItemStack getInput() { return source.getInput(); }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { cancelled = true; }
        public void setCustomOutputs(ItemStack... outputs) {
            this.outputs = outputs;
            this.outputsSet = true;
            this.cancelled = true;
        }
        public ItemStack[] getCustomOutputs() { return outputs; }
        public boolean hasCustomOutputs() { return outputsSet; }
    }

    public static final class EngineeringTableScriptEvent implements KubeEvent {
        private final EngineeringTableEvent source;
        private boolean cancelled;
        private String cancelMessage = "";

        private EngineeringTableScriptEvent(EngineeringTableEvent source) { this.source = source; }
        public net.minecraft.world.level.Level getLevel() { return source.getLevel(); }
        public ItemStack getHelmet() { return source.getHelmet(); }
        public ItemStack getGoggles() { return source.getGoggles(); }
        public ItemStack getSpyglass() { return source.getSpyglass(); }
        public EngineeringTableEvent.Type getType() { return source.getType(); }
        public boolean isAttach() { return source.isAttach(); }
        public boolean isDetach() { return source.isDetach(); }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { cancelled = true; }
        public void cancel(String message) { cancelled = true; cancelMessage = message; }
        public String getCancelMessage() { return cancelMessage; }
    }
}
