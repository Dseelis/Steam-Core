package com.steam.steamcore.client.event;

import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.client.gui.PackInfoScreen;
import com.steam.steamcore.client.util.GitHubDataFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = SteamCore.MODID, value = Dist.CLIENT)
public class MainMenuButtonEvent {

    private static final ResourceLocation BUTTON_ICON =
            ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "textures/screens/pack_button_preview.png");

    private static final int GAP = 4;
    private static boolean updateToastShown = false;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen titleScreen)) return;

        // Kick off fetch as early as possible
        GitHubDataFetcher.startFetchIfNeeded();

        checkAndUpdateToast();

        if (!Config.SHOW_MENU_BUTTON.get()) return;

        Minecraft mc = Minecraft.getInstance();
        int screenW  = titleScreen.width;
        int screenH  = titleScreen.height;

        int buttonColW = 200;
        int buttonX    = screenW / 2 - buttonColW / 2;
        int multiY     = screenH / 4 + 48 + 24;

        int iconX = buttonX + buttonColW + GAP;
        int iconY = multiY - 12;

        PackInfoButton btn = new PackInfoButton(iconX, iconY, 20, 20,
                Component.empty(),
                b -> mc.setScreen(new PackInfoScreen(titleScreen))
        );
        btn.setTooltip(Tooltip.create(Component.translatable("steamcore.menu.button.tooltip")));

        event.addListener(btn);
    }

    public static void checkAndUpdateToast() {
        if (updateToastShown) return;
        if (!Config.ENABLE_UPDATE_NOTIFICATIONS.get()) return;

        if (GitHubDataFetcher.getReleaseStatus() == GitHubDataFetcher.Status.DONE
                && GitHubDataFetcher.isUpdateAvailable()) {
            updateToastShown = true;
            Minecraft mc = Minecraft.getInstance();
            if (mc.getToasts() != null) {
                SystemToast.add(
                        mc.getToasts(),
                        SystemToast.SystemToastId.NARRATOR_TOGGLE,
                        Component.translatable("steamcore.update.toast.title"),
                        Component.translatable("steamcore.update.toast.message")
                );
            }
        }
    }

    private static class PackInfoButton extends Button {
        public PackInfoButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(gfx, mouseX, mouseY, partialTick);

            int drawX = getX() + 2;
            int drawY = getY() + 2;
            gfx.blit(BUTTON_ICON, drawX, drawY, 0f, 0f, 16, 16, 16, 16);
        }
    }
}
