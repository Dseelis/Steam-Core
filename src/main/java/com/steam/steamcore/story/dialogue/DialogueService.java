package com.steam.steamcore.story.dialogue;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.network.StoryPayloads;
import com.steam.steamcore.story.StoryService;
import com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents;
import com.steam.steamcore.story.AwakeningCutsceneHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class DialogueService {
    public static final String ENTITY_DIALOGUE_KEY = "steamcore_dialogue_id";
    private static final String ACTIVE_DIALOGUE_KEY = "steamcore_active_dialogue_id";

    private DialogueService() {}

    public static boolean open(ServerPlayer player, String dialogueId) {
        String json = DialogueRegistry.toJson(dialogueId);
        if (json == null) {
            SteamCore.LOGGER.warn("Unknown dialogue id requested: {}", dialogueId);
            return false;
        }
        player.getPersistentData().putString(ACTIVE_DIALOGUE_KEY, dialogueId);
        PacketDistributor.sendToPlayer(player, new StoryPayloads.OpenDialogue(dialogueId, json));
        return true;
    }

    public static boolean openAwakening(ServerPlayer player, String dialogueId) {
        String json = DialogueRegistry.toJson(dialogueId);
        if (json == null) {
            SteamCore.LOGGER.warn("Unknown awakening dialogue id requested: {}", dialogueId);
            return false;
        }
        player.getPersistentData().putString(ACTIVE_DIALOGUE_KEY, dialogueId);
        AwakeningCutsceneHandler.start(player);
        PacketDistributor.sendToPlayer(player, new StoryPayloads.StartAwakening(dialogueId, json));
        return true;
    }

    public static void finishAwakening(ServerPlayer player, String dialogueId) {
        if (!dialogueId.equals(player.getPersistentData().getString(ACTIVE_DIALOGUE_KEY))) return;
        if (!player.getPersistentData().getBoolean(com.steam.steamcore.utils.PlayerDataKeys.INTRO_PENDING)) return;
        AwakeningCutsceneHandler.finish(player);
    }

    public static void choose(ServerPlayer player, String dialogueId, String nodeId, String actionId) {
        if (!dialogueId.equals(player.getPersistentData().getString(ACTIVE_DIALOGUE_KEY))) return;
        DialogueDefinition definition = DialogueRegistry.get(dialogueId);
        if (definition == null) return;

        DialogueNode node;
        try {
            node = definition.node(nodeId);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        if (actionId == null) return;
        boolean validChoice = node.options().stream()
                .anyMatch(option -> actionId.equals(option.actionId()));
        if (!validChoice) return;
        player.getPersistentData().remove(ACTIVE_DIALOGUE_KEY);

        if ((StoryService.ENABLE_PROGRESSION.equals(actionId)
                || StoryService.DISABLE_PROGRESSION.equals(actionId))) {
            if (DialogueDefinition.MECHANIC_INTRO_ID.equals(dialogueId)
                    || DialogueDefinition.MECHANIC_MAIN_ID.equals(dialogueId)) {
                StoryService.chooseDifficulty(player, actionId);
            }
            return;
        }

        if (SteamCoreKubeJSEvents.isKubeJSLoaded()) {
            SteamCoreKubeJSEvents.fireDialogueChoiceEvent(player, dialogueId, nodeId, actionId);
        }
    }

    public static void openNpcDialogue(ServerPlayer player, String dialogueId) {
        open(player, dialogueId);
    }
}
