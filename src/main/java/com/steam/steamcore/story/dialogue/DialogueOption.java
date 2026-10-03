package com.steam.steamcore.story.dialogue;

public record DialogueOption(String label, String nextNode, String actionId) {
    public static DialogueOption next(String label, String nodeId) {
        return new DialogueOption(label, nodeId, null);
    }

    public static DialogueOption action(String label, String actionId) {
        return new DialogueOption(label, null, actionId);
    }
}
