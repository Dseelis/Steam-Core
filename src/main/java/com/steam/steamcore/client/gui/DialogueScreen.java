package com.steam.steamcore.client.gui;

import com.steam.steamcore.network.StoryPayloads;
import com.steam.steamcore.story.dialogue.DialogueDefinition;
import com.steam.steamcore.story.dialogue.DialogueNode;
import com.steam.steamcore.story.dialogue.DialogueOption;
import com.steam.steamcore.story.dialogue.DialogueText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/** Novella-style screen that can display any node from a DialogueDefinition. */
public class DialogueScreen extends Screen {
    private static final int KEY_ESCAPE = 256;
    private final DialogueDefinition definition;
    private final String dialogueId;
    private DialogueNode node;
    private float revealTicks;
    private final List<Button> optionButtons = new ArrayList<>();

    public DialogueScreen(String dialogueId, DialogueDefinition definition) {
        super(Component.translatable("screen.steamcore.dialogue"));
        this.dialogueId = dialogueId;
        this.definition = definition;
        setNode(definition.startNode());
    }

    private void setNode(String id) {
        this.node = definition.node(id);
        this.revealTicks = 0;
        if (minecraft != null) buildOptionButtons();
    }

    @Override
    protected void init() {
        buildOptionButtons();
    }

    private void buildOptionButtons() {
        clearWidgets();
        optionButtons.clear();
        int panelWidth = Math.min(560, width - 24);
        int panelHeight = Math.min(260, height - 24);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        int gap = 8;
        int buttonWidth = Math.min(250, (panelWidth - 40 - gap * (node.options().size() - 1))
                / Math.max(1, node.options().size()));
        int totalWidth = buttonWidth * node.options().size() + gap * (node.options().size() - 1);
        int buttonX = width / 2 - totalWidth / 2;
        int buttonY = panelY + panelHeight - 36;

        for (DialogueOption option : node.options()) {
            Button button = Button.builder(Component.literal(DialogueText.resolve(option.label(), languageCode())), ignored -> choose(option))
                    .bounds(buttonX, buttonY, buttonWidth, 24)
                    .build();
            button.visible = false;
            optionButtons.add(button);
            addRenderableWidget(button);
            buttonX += buttonWidth + gap;
        }
    }

    private void choose(DialogueOption option) {
        if (option.nextNode() != null) {
            setNode(option.nextNode());
            return;
        }
        if (option.actionId() != null) {
            PacketDistributor.sendToServer(new StoryPayloads.ChooseDialogueOption(
                    dialogueId, node.id(), option.actionId()));
            onClose();
        }
    }

    @Override
    public void tick() {
        revealTicks += 1;
        boolean complete = revealedCharacters() >= displayedText().codePointCount(0, displayedText().length());
        for (Button button : optionButtons) {
            button.visible = complete;
            button.active = complete;
        }
    }

    private int revealedCharacters() {
        return Math.min(displayedText().codePointCount(0, displayedText().length()), (int) (revealTicks * 2.0f));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && revealedCharacters() < displayedText().codePointCount(0, displayedText().length())) {
            revealTicks = displayedText().codePointCount(0, displayedText().length()) / 2.0f;
            for (Button optionButton : optionButtons) {
                optionButton.visible = true;
                optionButton.active = true;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Keep the 3D world sharp behind the dialogue instead of applying Minecraft's menu blur.
        graphics.fill(0, 0, width, height, 0x550A0C10);

        int panelWidth = Math.min(560, width - 24);
        int panelHeight = Math.min(260, height - 24);
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        graphics.fill(panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFF17191D);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF02B2D32);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + 3, 0xFFD69B52);

        drawMechanicPortrait(graphics, panelX + 18, panelY + 28);
        graphics.drawString(font, DialogueText.resolve(node.speaker(), languageCode()), panelX + 104, panelY + 24, 0xFFE8B76C, false);
        graphics.fill(panelX + 100, panelY + 39, panelX + panelWidth - 18, panelY + 40, 0xFF73604B);

        int charCount = revealedCharacters();
        String text = displayedText();
        int charEnd = text.offsetByCodePoints(0, charCount);
        Component visibleText = Component.literal(text.substring(0, charEnd));
        int textWidth = panelWidth - 142;
        List<net.minecraft.util.FormattedCharSequence> lines = font.split(visibleText, textWidth);
        int maxLines = Math.max(1, (panelHeight - 96) / 10);
        for (int i = 0; i < Math.min(lines.size(), maxLines); i++) {
            graphics.drawString(font, lines.get(i), panelX + 104, panelY + 51 + i * 10, 0xFFF0EEE9, false);
        }

        if (charCount < text.codePointCount(0, text.length())) {
            graphics.drawString(font, "▼", panelX + panelWidth - 25, panelY + panelHeight - 48,
                    0xFFD69B52, false);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // The vanilla background hook applies a full-screen blur in some 1.21 screen render paths.
        // This dialogue draws its own dim overlay and panel, so suppress that post-process.
    }

    private String languageCode() {
        return minecraft == null ? "en_us" : minecraft.options.languageCode;
    }

    private String displayedText() {
        return DialogueText.resolve(node.text(), languageCode());
    }

    private void drawMechanicPortrait(GuiGraphics graphics, int x, int y) {
        int bob = (int) (Math.sin((revealTicks + 1) * 0.12) * 2.0);
        y += bob;
        graphics.fill(x, y, x + 72, y + 94, 0xFF15171A);
        graphics.fill(x + 3, y + 3, x + 69, y + 91, 0xFF55483D);
        // Temporary pixel-art silhouette; replace with the mechanic's portrait texture later.
        graphics.fill(x + 20, y + 14, x + 52, y + 44, 0xFFC79B70);
        graphics.fill(x + 18, y + 10, x + 54, y + 23, 0xFF34383B);
        graphics.fill(x + 17, y + 19, x + 23, y + 38, 0xFF34383B);
        graphics.fill(x + 49, y + 20, x + 55, y + 38, 0xFF34383B);
        graphics.fill(x + 24, y + 26, x + 30, y + 31, 0xFF3A3029);
        graphics.fill(x + 42, y + 26, x + 48, y + 31, 0xFF3A3029);
        graphics.fill(x + 28, y + 49, x + 44, y + 57, 0xFFB98846);
        graphics.fill(x + 13, y + 57, x + 59, y + 87, 0xFF34424A);
        graphics.fill(x + 5, y + 66, x + 15, y + 73, 0xFFC5C9C7);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KEY_ESCAPE) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(null);
    }
}
