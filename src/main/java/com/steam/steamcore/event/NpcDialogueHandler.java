package com.steam.steamcore.event;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.story.dialogue.DialogueService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public final class NpcDialogueHandler {
    private NpcDialogueHandler() {}

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer player)) return;
        String dialogueId = event.getTarget().getPersistentData().getString(DialogueService.ENTITY_DIALOGUE_KEY);
        if (dialogueId.isBlank()) return;
        DialogueService.openNpcDialogue(player, dialogueId);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
