package com.steam.steamcore.story.dialogue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DialogueRegistry {
    private static final Map<String, DialogueDefinition> DIALOGUES = new ConcurrentHashMap<>();

    static {
        DIALOGUES.put(DialogueDefinition.MECHANIC_INTRO_ID, DialogueDefinition.MECHANIC_PROLOGUE);
        DIALOGUES.put(DialogueDefinition.MECHANIC_MAIN_ID, DialogueDefinition.MECHANIC_MAIN);
    }

    private DialogueRegistry() {}

    public static void register(String id, String json) {
        DialogueDefinition definition = DialogueDefinition.fromJson(json);
        DIALOGUES.put(id, definition);
    }

    public static DialogueDefinition get(String id) {
        return DIALOGUES.get(id);
    }

    public static String toJson(String id) {
        DialogueDefinition definition = DIALOGUES.get(id);
        return definition == null ? null : definition.toJson();
    }
}
