package com.steam.steamcore.client.gui;

import net.minecraft.client.gui.GuiGraphics;

// Shared warm industrial palette for SteamCore screens.
final class SteamCoreGuiStyle {
    static final int FRAME = 0xFF282522;
    static final int PANEL = 0xFF3C3836;
    static final int PANEL_LIGHT = 0xFF504945;
    static final int SLOT = 0xFF1D2021;
    static final int BORDER = 0xFF1D2021;
    static final int BORDER_LIGHT = 0xFF7C6F64;
    static final int BRASS = 0xFFD79921;
    static final int BRASS_DARK = 0xFFAF7A18;
    static final int TEXT = 0xFFEBDBB2;
    static final int TEXT_MUTED = 0xFFD5C4A1;

    private SteamCoreGuiStyle() {}

    static void drawFrame(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, FRAME);
        graphics.renderOutline(x, y, width, height, BORDER);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 17, PANEL_LIGHT);
        graphics.fill(x + 1, y + 17, x + width - 1, y + 18, BRASS_DARK);
        graphics.fill(x + 1, y + 18, x + width - 1, y + height - 1, PANEL);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 3, BORDER_LIGHT);
    }

    static void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, PANEL_LIGHT);
        graphics.renderOutline(x, y, width, height, BORDER);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, PANEL);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, BORDER_LIGHT);
    }

    static void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, BORDER);
        graphics.fill(x, y, x + 16, y + 16, SLOT);
        graphics.fill(x, y, x + 16, y + 1, BORDER_LIGHT);
        graphics.fill(x, y, x + 1, y + 16, BORDER_LIGHT);
    }
}
