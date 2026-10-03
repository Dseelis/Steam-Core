package com.steam.steamcore.story.dialogue;

import java.util.List;

public record DialogueNode(String id, String speaker, String text, List<DialogueOption> options) {
    public DialogueNode {
        options = List.copyOf(options);
    }
}
