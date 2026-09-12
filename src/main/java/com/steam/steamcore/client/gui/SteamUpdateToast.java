package com.steam.steamcore.client.gui;

import com.steam.steamcore.SteamCore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SteamUpdateToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/system");
    private static final ResourceLocation ICON =
            ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "textures/screens/pack_button_preview.png");
    private static final Object TOKEN = new Object();

    private final Component title;
    private final Component message;
    private final long displayTime;

    public SteamUpdateToast(Component title, Component message) {
        this(title, message, 6000L);
    }

    public SteamUpdateToast(Component title, Component message, long displayTime) {
        this.title = title;
        this.message = message;
        this.displayTime = displayTime;
    }

    @Override
    public Object getToken() {
        return TOKEN;
    }

    @Override
    public int width() {
        return 160;
    }

    @Override
    public int height() {
        return 32;
    }

    @Override
    public Toast.Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

        guiGraphics.blit(ICON, 7, 8, 0f, 0f, 16, 16, 16, 16);

        guiGraphics.drawString(toastComponent.getMinecraft().font, this.title, 28, 7, 0xFFE8C96A, false);

        guiGraphics.drawString(toastComponent.getMinecraft().font, this.message, 28, 18, 0xFFFFFFFF, false);

        double totalTime = (double) this.displayTime * toastComponent.getNotificationDisplayTimeMultiplier();
        return (double) timeSinceLastVisible < totalTime ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }
}
