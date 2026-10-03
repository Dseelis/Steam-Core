package com.steam.steamcore.client.network;

import com.steam.steamcore.client.gui.DialogueScreen;
import com.steam.steamcore.client.gui.AwakeningCutsceneScreen;
import com.steam.steamcore.story.dialogue.DialogueDefinition;
import com.steam.steamcore.client.story.AwakeningCutsceneController;
import net.minecraft.client.Minecraft;

public final class ClientStoryPayloads {
    private ClientStoryPayloads() {}

    public static void openDialogue(String dialogueId, String definitionJson) {
        Minecraft.getInstance().setScreen(new DialogueScreen(
                dialogueId, DialogueDefinition.fromJson(definitionJson)));
    }

    public static void startAwakening(String dialogueId, String definitionJson) {
        AwakeningCutsceneController.start();
        Minecraft.getInstance().setScreen(new AwakeningCutsceneScreen(
                dialogueId, DialogueDefinition.fromJson(definitionJson)));
    }
}
