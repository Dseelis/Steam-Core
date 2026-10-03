package com.steam.steamcore.story.dialogue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reusable dialogue data: node id, speaker, line, and selectable branches/actions. */
public final class DialogueDefinition {
    public static final String MECHANIC_INTRO_ID = "steamcore:mechanic_intro";
    public static final String MECHANIC_MAIN_ID = "steamcore:mechanic_main";
    public static final DialogueDefinition MECHANIC_PROLOGUE = new DialogueDefinition(List.of(
            new DialogueNode("awakening", "$dialogue.steamcore.mechanic.intro.awakening.speaker",
                    "$dialogue.steamcore.mechanic.intro.awakening.text",
                    List.of(DialogueOption.next("$dialogue.steamcore.mechanic.intro.awakening.next", "difficulty"))),
            new DialogueNode("difficulty", "$dialogue.steamcore.mechanic.intro.difficulty.speaker",
                    "$dialogue.steamcore.mechanic.intro.difficulty.text",
                    List.of(
                            DialogueOption.action("$dialogue.steamcore.mechanic.intro.enable", "difficulty_enable"),
                            DialogueOption.action("$dialogue.steamcore.mechanic.intro.disable", "difficulty_disable")
                    ))
    ));
    public static final DialogueDefinition MECHANIC_MAIN = new DialogueDefinition(List.of(
            new DialogueNode("welcome", "$dialogue.steamcore.mechanic.main.speaker",
                    "$dialogue.steamcore.mechanic.main.welcome",
                    List.of(
                            DialogueOption.next("$dialogue.steamcore.mechanic.main.core.choice", "core"),
                            DialogueOption.next("$dialogue.steamcore.mechanic.main.paradox.choice", "paradox"),
                            DialogueOption.next("$dialogue.steamcore.mechanic.main.progression.choice", "progression"),
                            DialogueOption.action("$dialogue.steamcore.mechanic.main.close", "mechanic_close")
                    )),
            new DialogueNode("core", "$dialogue.steamcore.mechanic.main.speaker",
                    "$dialogue.steamcore.mechanic.main.core.text",
                    List.of(DialogueOption.next("$dialogue.steamcore.mechanic.main.back", "welcome"))),
            new DialogueNode("paradox", "$dialogue.steamcore.mechanic.main.speaker",
                    "$dialogue.steamcore.mechanic.main.paradox.text",
                    List.of(DialogueOption.next("$dialogue.steamcore.mechanic.main.back", "welcome"))),
            new DialogueNode("progression", "$dialogue.steamcore.mechanic.main.speaker",
                    "$dialogue.steamcore.mechanic.main.progression.text",
                    List.of(
                            DialogueOption.action("$dialogue.steamcore.mechanic.intro.enable", "difficulty_enable"),
                            DialogueOption.action("$dialogue.steamcore.mechanic.intro.disable", "difficulty_disable"),
                            DialogueOption.next("$dialogue.steamcore.mechanic.main.back", "welcome")
                    ))
    ));

    private final List<DialogueNode> nodeList;
    private final Map<String, DialogueNode> nodes;
    private final String startNode;

    public DialogueDefinition(List<DialogueNode> nodes) {
        this(nodes, nodes.isEmpty() ? "" : nodes.get(0).id());
    }

    public DialogueDefinition(List<DialogueNode> nodes, String startNode) {
        Map<String, DialogueNode> byId = new LinkedHashMap<>();
        for (DialogueNode node : nodes) {
            if (byId.put(node.id(), node) != null) {
                throw new IllegalArgumentException("Duplicate dialogue node: " + node.id());
            }
        }
        if (!byId.containsKey(startNode)) throw new IllegalArgumentException("Unknown start node: " + startNode);
        this.nodeList = List.copyOf(nodes);
        this.nodes = Map.copyOf(byId);
        this.startNode = startNode;
    }

    public DialogueNode node(String id) {
        DialogueNode node = nodes.get(id);
        if (node == null) throw new IllegalArgumentException("Unknown dialogue node: " + id);
        return node;
    }

    public String startNode() {
        return startNode;
    }

    public static DialogueDefinition fromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String start = root.get("start").getAsString();
        JsonArray nodeArray = root.getAsJsonArray("nodes");
        List<DialogueNode> nodes = new ArrayList<>();
        for (JsonElement element : nodeArray) {
            JsonObject node = element.getAsJsonObject();
            List<DialogueOption> options = new ArrayList<>();
            for (JsonElement choiceElement : node.getAsJsonArray("choices")) {
                JsonObject choice = choiceElement.getAsJsonObject();
                String next = choice.has("next") ? choice.get("next").getAsString() : null;
                String action = choice.has("action") ? choice.get("action").getAsString() : null;
                if ((next == null) == (action == null)) {
                    throw new IllegalArgumentException("Each dialogue choice needs exactly one of 'next' or 'action'");
                }
                options.add(new DialogueOption(DialogueText.fromJson(choice.get("label")), next, action));
            }
            nodes.add(new DialogueNode(node.get("id").getAsString(), DialogueText.fromJson(node.get("speaker")),
                    DialogueText.fromJson(node.get("text")), options));
        }
        DialogueDefinition result = new DialogueDefinition(nodes, start);
        for (DialogueNode node : nodes) {
            for (DialogueOption option : node.options()) {
                if (option.nextNode() != null) result.node(option.nextNode());
            }
        }
        return result;
    }

    public String toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("start", startNode);
        JsonArray nodeArray = new JsonArray();
        for (DialogueNode node : nodeList) {
            JsonObject nodeJson = new JsonObject();
            nodeJson.addProperty("id", node.id());
            nodeJson.add("speaker", DialogueText.toJson(node.speaker()));
            nodeJson.add("text", DialogueText.toJson(node.text()));
            JsonArray choices = new JsonArray();
            for (DialogueOption option : node.options()) {
                JsonObject choice = new JsonObject();
                choice.add("label", DialogueText.toJson(option.label()));
                if (option.nextNode() != null) choice.addProperty("next", option.nextNode());
                if (option.actionId() != null) choice.addProperty("action", option.actionId());
                choices.add(choice);
            }
            nodeJson.add("choices", choices);
            nodeArray.add(nodeJson);
        }
        root.add("nodes", nodeArray);
        return root.toString();
    }
}
