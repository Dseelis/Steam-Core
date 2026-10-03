package com.steam.steamcore.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.item.ItemStack;

// KubeJS-only adapter, loaded only when KubeJS discovers the plugin descriptor.
public final class SteamCoreKubeJSPlugin implements KubeJSPlugin {
    private static final EventGroup GROUP = EventGroup.of("SteamCoreEvents");
    private static final EventHandler DISASSEMBLY = GROUP.server("disassembly", () -> DisassemblyScriptEvent.class);
    private static final EventHandler ENGINEERING_TABLE = GROUP.server("engineeringTable", () -> EngineeringTableScriptEvent.class);
    private static final EventHandler MOB_LEVEL = GROUP.server("mobLevel", () -> MobLevelScriptEvent.class);
    private static final EventHandler BOSS_PROGRESS = GROUP.server("bossProgress", () -> BossProgressScriptEvent.class);
    private static final EventHandler DIALOGUE_CHOICE = GROUP.server("dialogueChoice", () -> DialogueChoiceScriptEvent.class);

    @Override
    public void registerBindings(dev.latvian.mods.kubejs.script.BindingRegistry bindings) {
        bindings.add("SteamCoreStory", SteamCoreKubeJSApi.class);
    }

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
        } else if ("mobLevel".equals(name) && source instanceof MobLevelHookEvent event) {
            MobLevelScriptEvent scriptEvent = new MobLevelScriptEvent(event);
            MOB_LEVEL.post(ScriptType.SERVER, scriptEvent);
            event.setLevel(scriptEvent.level);
            event.setCancelled(scriptEvent.cancelled);
        } else if ("bossProgress".equals(name) && source instanceof BossProgressHookEvent event) {
            BossProgressScriptEvent scriptEvent = new BossProgressScriptEvent(event);
            BOSS_PROGRESS.post(ScriptType.SERVER, scriptEvent);
            event.setPoints(scriptEvent.points);
            event.setCancelled(scriptEvent.cancelled);
        } else if ("dialogueChoice".equals(name) && source instanceof DialogueChoiceHookEvent event) {
            DIALOGUE_CHOICE.post(ScriptType.SERVER, new DialogueChoiceScriptEvent(event));
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

    public static final class MobLevelScriptEvent implements KubeEvent {
        private final MobLevelHookEvent source;
        private int level;
        private boolean cancelled;
        private MobLevelScriptEvent(MobLevelHookEvent source) { this.source = source; this.level = source.getLevelValue(); }
        @Info("World containing the mob") public net.minecraft.world.level.Level getLevel() { return source.getLevel(); }
        @Info("Hostile mob being assigned a level") public net.minecraft.world.entity.Mob getMob() { return source.getMob(); }
        @Info("Proposed mob level") public int getMobLevel() { return level; }
        public void setMobLevel(int level) { this.level = Math.max(1, Math.min(10, level)); }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { cancelled = true; }
    }

    public static final class BossProgressScriptEvent implements KubeEvent {
        private final BossProgressHookEvent source;
        private int points;
        private boolean cancelled;
        private BossProgressScriptEvent(BossProgressHookEvent source) { this.source = source; this.points = source.getPoints(); }
        public net.minecraft.world.level.Level getLevel() { return source.getLevel(); }
        public net.minecraft.world.entity.LivingEntity getBoss() { return source.getBoss(); }
        public String getBossId() { return source.getBossId(); }
        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = Math.max(0, points); }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { cancelled = true; }
    }

    public static final class DialogueChoiceScriptEvent implements KubeEvent {
        private final DialogueChoiceHookEvent source;
        private DialogueChoiceScriptEvent(DialogueChoiceHookEvent source) { this.source = source; }
        public net.minecraft.server.level.ServerPlayer getPlayer() { return source.getPlayer(); }
        public String getDialogueId() { return source.getDialogueId(); }
        public String getNodeId() { return source.getNodeId(); }
        public String getActionId() { return source.getActionId(); }
    }
}
