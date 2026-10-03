package com.steam.steamcore.integration.kubejs;

import net.minecraft.server.level.ServerPlayer;

public record DialogueChoiceHookEvent(ServerPlayer player, String dialogueId, String nodeId, String actionId) {
    public ServerPlayer getPlayer() { return player; }
    public String getDialogueId() { return dialogueId; }
    public String getNodeId() { return nodeId; }
    public String getActionId() { return actionId; }
}
