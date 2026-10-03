package com.steam.steamcore.client.story;

import com.steam.steamcore.SteamCore;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SteamCore.MODID, value = Dist.CLIENT)
public final class AwakeningCutsceneClientEvents {
    private AwakeningCutsceneClientEvents() {}

    @SubscribeEvent
    public static void hideGameHudDuringCutscene(RenderGuiEvent.Pre event) {
        if (AwakeningCutsceneController.isActive()) event.setCanceled(true);
    }
}
