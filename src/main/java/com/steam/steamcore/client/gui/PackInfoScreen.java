package com.steam.steamcore.client.gui;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.client.util.GitHubDataFetcher;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class PackInfoScreen extends Screen {

    // Resources
    private static final ResourceLocation ICON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "textures/screens/pack_info_icon.png");

    // URLs
    private static final String URL_GITHUB     = "https://github.com/Dseelis/Steamcreate2";
    private static final String URL_CURSEFORGE = "https://www.curseforge.com/minecraft/modpacks/steamcreate2";

    // Colours
    private static final int COL_BG_LEFT    = 0xFF1A1A2E;
    private static final int COL_BG_RIGHT   = 0xFF16213E;
    private static final int COL_PANEL      = 0xCC0F3460;
    private static final int COL_BORDER     = 0xFF533483;
    private static final int COL_TITLE      = 0xFFE8C96A; // gold
    private static final int COL_VERSION    = 0xFFAAAAAA;
    private static final int COL_TEXT       = 0xFFCCCCCC;
    private static final int COL_UPDATE_OK  = 0xFF55FF55;
    private static final int COL_UPDATE_NEW = 0xFFFFAA00;
    private static final int COL_HEADING    = 0xFFE8C96A;
    private static final int COL_SCROLL_BG  = 0x55000000;
    private static final int COL_SCROLL_BAR = 0xFF533483;

    private static final int DIVIDER_X_RATIO = 40; // left panel = 40% of width

    private final Screen parent;

    // Changelog scroll
    private record ChangelogLine(String text, int color) {}
    private final List<ChangelogLine> changelogEntries = new ArrayList<>();
    private int scrollOffset   = 0;
    private int maxScrollOffset = 0;
    private boolean changelogBuilt = false;

    // Calculated layout
    private int dividerX;
    private int rightPanelX, rightPanelW;
    private int changelogY, changelogH;
    private int lineH = 9;

    // Animation
    private float fadeAlpha = 0f;

    public PackInfoScreen(Screen parent) {
        super(Component.translatable("steamcore.packinfo.title"));
        this.parent = parent;
        GitHubDataFetcher.startFetchIfNeeded();
    }

    // Layout

    @Override
    protected void init() {
        dividerX     = width * DIVIDER_X_RATIO / 100;
        rightPanelX  = dividerX + 8;
        rightPanelW  = width - rightPanelX - 8;
        changelogY   = 38;
        changelogH   = height - changelogY - 30;

        // Left panel buttons
        int btnW = Math.min(dividerX - 20, 130);
        int btnX = 10;
        int btnY = height - 60;

        addRenderableWidget(Button.builder(
                        Component.translatable("steamcore.packinfo.github"),
                        btn -> openUrl(URL_GITHUB))
                .bounds(btnX, btnY, btnW / 2 - 2, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.translatable("steamcore.packinfo.curseforge"),
                        btn -> openUrl(URL_CURSEFORGE))
                .bounds(btnX + btnW / 2 + 2, btnY, btnW / 2 - 2, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.translatable("gui.back"),
                        btn -> onClose())
                .bounds(width / 2 - 60, height - 26, 120, 20)
                .build());
    }

    // Render

    @Override
    public void renderBackground(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        // intentionally empty — we draw our own background in render()
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = width, h = height;

        // background
        gfx.fillGradient(0, 0, dividerX, h, COL_BG_LEFT, 0xFF0D0D1A);
        gfx.fillGradient(dividerX, 0, w, h, COL_BG_RIGHT, 0xFF0A1628);

        // Vertical divider line
        gfx.fill(dividerX, 0, dividerX + 1, h, COL_BORDER);

        // Panel content
        renderLeftPanel(gfx, mouseX, mouseY);
        renderRightPanel(gfx, mouseX, mouseY);

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private void renderLeftPanel(GuiGraphics gfx, int mouseX, int mouseY) {
        int cx = dividerX / 2;
        int y  = 20;

        // Icon
        gfx.blit(ICON_TEXTURE, cx - 16, y, 0, 0, 32, 32, 32, 32);
        y += 40;

        // Pack name
        String packName = SteamCore.getPackName();
        gfx.drawCenteredString(font, Component.literal(packName).withColor(COL_TITLE), cx, y, COL_TITLE);
        y += 14;

        // Mod version
        String verText = "v" + SteamCore.getPackVersion();
        gfx.drawCenteredString(font, verText, cx, y, COL_VERSION);
        y += 10;

        // Update status
        renderUpdateStatus(gfx, cx, y);
        y += 22;

        // Description
        String desc = "By Dseelis";
        gfx.drawCenteredString(font, desc, cx, y, COL_TEXT);
        y += 10;

        String desc2 = "NeoForge 1.21.1";
        gfx.drawCenteredString(font, desc2, cx, y, 0xFF777777);
    }

    private void renderUpdateStatus(GuiGraphics gfx, int cx, int y) {
        GitHubDataFetcher.Status status = GitHubDataFetcher.getReleaseStatus();
        String line;
        int colour;
        if (status == GitHubDataFetcher.Status.LOADING) {
            line   = "⟳ " + getLang("steamcore.packinfo.checking");
            colour = 0xFF888888;
        } else if (status == GitHubDataFetcher.Status.ERROR) {
            line   = "✗ " + getLang("steamcore.packinfo.no_connection");
            colour = 0xFFFF5555;
        } else if (GitHubDataFetcher.isUpdateAvailable()) {
            String tag = GitHubDataFetcher.getLatestTag();
            line   = "⬆ " + getLang("steamcore.packinfo.update_available");
            colour = COL_UPDATE_NEW;
        } else {
            line   = "✔ " + getLang("steamcore.packinfo.up_to_date");
            colour = COL_UPDATE_OK;
        }
        gfx.drawCenteredString(font, line, cx, y, colour);
    }

    private void renderRightPanel(GuiGraphics gfx, int mouseX, int mouseY) {
        int px = rightPanelX, py = 10, pw = rightPanelW;

        // Section heading
        gfx.drawString(font, getLang("steamcore.packinfo.changelog"), px, py, COL_HEADING, false);

        // Rebuild lines if changelog just arrived
        rebuildChangelogLinesIfNeeded();

        // Scroll area background
        gfx.fill(px, changelogY, px + pw, changelogY + changelogH, COL_SCROLL_BG);

        // Clipped text rendering
        int visibleLines = changelogH / lineH;
        maxScrollOffset  = Math.max(0, changelogEntries.size() - visibleLines);
        scrollOffset     = Mth.clamp(scrollOffset, 0, maxScrollOffset);

        gfx.enableScissor(px, changelogY, px + pw - 8, changelogY + changelogH);
        GitHubDataFetcher.Status cls = GitHubDataFetcher.getChangelogStatus();
        if (cls == GitHubDataFetcher.Status.LOADING) {
            gfx.drawString(font, getLang("steamcore.packinfo.loading"), px + 4, changelogY + 4, 0xFF888888, false);
        } else if (cls == GitHubDataFetcher.Status.ERROR) {
            gfx.drawString(font, "✗ " + getLang("steamcore.packinfo.no_connection"), px + 4, changelogY + 4, 0xFFFF5555, false);
        } else {
            int textY = changelogY + 2;
            for (int i = scrollOffset; i < changelogEntries.size() && textY < changelogY + changelogH; i++) {
                ChangelogLine entry = changelogEntries.get(i);
                gfx.drawString(font, entry.text(), px + 4, textY, entry.color(), false);
                textY += lineH;
            }
        }
        gfx.disableScissor();

        // Scrollbar
        if (maxScrollOffset > 0) {
            int barH  = (int) ((float) visibleLines / changelogEntries.size() * changelogH);
            int barY  = changelogY + (int) ((float) scrollOffset / changelogEntries.size() * changelogH);
            gfx.fill(px + pw - 6, changelogY, px + pw - 4, changelogY + changelogH, 0x33FFFFFF);
            gfx.fill(px + pw - 6, barY, px + pw - 4, barY + barH, COL_SCROLL_BAR);
        }
    }

    // Changelog line

    private void rebuildChangelogLinesIfNeeded() {
        if (changelogBuilt) return;
        if (GitHubDataFetcher.getChangelogStatus() != GitHubDataFetcher.Status.DONE) return;

        String raw = GitHubDataFetcher.getChangelog();
        if (raw == null) return;

        changelogEntries.clear();
        int maxW = rightPanelW - 16;
        for (String rawLine : raw.split("\r?\n")) {
            String trimmed = rawLine.trim();
            int color = COL_TEXT;
            String content = rawLine;

            if (trimmed.startsWith("# ")) {
                color = COL_HEADING;
                content = trimmed.substring(2);
            } else if (trimmed.startsWith("## ")) {
                color = 0xFFBBBBFF;
                content = trimmed.substring(3);
            } else if (trimmed.startsWith("### ")) {
                color = 0xFF88D0FF;
                content = trimmed.substring(4);
            } else if (trimmed.startsWith("#### ")) {
                color = 0xFFAAD0FF;
                content = trimmed.substring(5);
            } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                color = 0xFFCCCCCC;
            } else if (trimmed.startsWith("//")) {
                color = 0xFF777777;
            } else if (trimmed.startsWith("(temp)")) {
                color = 0xFFCCCC66;
            }

            List<net.minecraft.util.FormattedCharSequence> wrapped =
                    font.split(Component.literal(content), maxW);
            if (wrapped.isEmpty()) {
                changelogEntries.add(new ChangelogLine("", color));
            } else {
                for (var seq : wrapped) {
                    StringBuilder sb = new StringBuilder();
                    seq.accept((idx, style, cp) -> { sb.appendCodePoint(cp); return true; });
                    changelogEntries.add(new ChangelogLine(sb.toString(), color));
                }
            }
        }
        changelogBuilt = true;
    }

    // Input

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX > rightPanelX) {
            scrollOffset -= (int) Math.signum(scrollY) * 3;
            scrollOffset  = Mth.clamp(scrollOffset, 0, maxScrollOffset);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 264) { scrollOffset = Math.min(scrollOffset + 3, maxScrollOffset); return true; } // Down
        if (keyCode == 265) { scrollOffset = Math.max(scrollOffset - 3, 0); return true; }               // Up
        if (keyCode == 268) { scrollOffset = 0; return true; }                                           // Home
        if (keyCode == 269) { scrollOffset = maxScrollOffset; return true; }                             // End
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private void openUrl(String url) {
        try {
            Util.getPlatform().openUri(new java.net.URI(url));
        } catch (Exception e) {
            SteamCore.LOGGER.warn("[SteamCore] Failed to open URL: {}", url);
        }
    }

    private String getLang(String key) {
        return Component.translatable(key).getString();
    }
}
