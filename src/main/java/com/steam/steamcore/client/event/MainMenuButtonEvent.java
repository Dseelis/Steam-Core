package com.steam.steamcore.client.event;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.client.gui.PackInfoScreen;
import com.steam.steamcore.client.util.GitHubDataFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Injects a small 16×16 icon button to the right of the Multiplayer button
 * on the vanilla TitleScreen. Controlled by {@code Config.SHOW_MENU_BUTTON}.
 */
@EventBusSubscriber(modid = SteamCore.MODID, value = Dist.CLIENT)
public class MainMenuButtonEvent {

    /** The button sprite — matches the 16×16 texture we ship. */
    private static final ResourceLocation BUTTON_ICON =
            ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "textures/screens/pack_info_icon.png");

    /** Gap (in pixels) between the right edge of the Multiplayer button and our icon. */
    private static final int GAP = 4;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen titleScreen)) return;
        if (!Config.SHOW_MENU_BUTTON.get()) return;

        // Kick off the async GitHub fetch as early as possible
        GitHubDataFetcher.startFetchIfNeeded();

        Minecraft mc = Minecraft.getInstance();
        int screenW  = titleScreen.width;
        int screenH  = titleScreen.height;

        // Vanilla TitleScreen lays out its main buttons as a column centred at
        // (width/2) with width=200 and starting Y around height/4+48.
        // The Multiplayer button is the 3rd button (index 2) in that group.
        // We replicate the same Y calculation and place our icon immediately
        // to the right of the button block (width/2 + 100 + gap).

        int buttonColW = 200;
        int buttonX    = screenW / 2 - buttonColW / 2;          // left edge of button column
        int multiY     = screenH / 4 + 48 + 24;                 // Y of the "Multiplayer" button (same as vanilla)

        // Our icon sits to the right: (buttonX + buttonColW + gap), vertically centred on the button (height 20)
        int iconX = buttonX + buttonColW + GAP;
        int iconY = multiY + (20 - 16) / 2;                     // centre 16px in 20px-tall button row

        ImageButton btn = new ImageButton(
                iconX, iconY, 16, 16,
                // WidgetSprites: normal, highlighted  — we use the same texture for both
                new net.minecraft.client.gui.components.WidgetSprites(
                        BUTTON_ICON, BUTTON_ICON
                ),
                b -> mc.setScreen(new PackInfoScreen(titleScreen)),
                Component.translatable("steamcore.menu.button")
        );
        btn.setTooltip(Tooltip.create(Component.translatable("steamcore.menu.button.tooltip")));

        event.addListener(btn);
    }
}
