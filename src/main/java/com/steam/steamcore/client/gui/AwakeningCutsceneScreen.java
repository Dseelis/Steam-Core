package com.steam.steamcore.client.gui;

import com.steam.steamcore.client.story.AwakeningCutsceneController;
import com.steam.steamcore.network.StoryPayloads;
import com.steam.steamcore.story.dialogue.DialogueDefinition;
import com.steam.steamcore.story.dialogue.DialogueText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/** Lightweight subtitle layer over the live 3D world and its animated NPCs. */
public final class AwakeningCutsceneScreen extends Screen {
    private static final int DURATION_TICKS = 168;
    private final String dialogueId;
    private final DialogueDefinition definition;
    private int ticks;
    private boolean finished;

    public AwakeningCutsceneScreen(String dialogueId, DialogueDefinition definition) {
        super(Component.translatable("screen.steamcore.awakening"));
        this.dialogueId = dialogueId;
        this.definition = definition;
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.translatable("screen.steamcore.skip"), button -> finish())
                .bounds(width - 104, height - 32, 92, 20).build());
    }

    @Override
    public void tick() {
        ticks++;
        AwakeningCutsceneController.tick();
        if (ticks >= DURATION_TICKS) finish();
    }

    private void finish() {
        if (finished || minecraft == null) return;
        finished = true;
        AwakeningCutsceneController.stop();
        PacketDistributor.sendToServer(new StoryPayloads.FinishAwakening(dialogueId));
        minecraft.setScreen(new DialogueScreen(dialogueId, definition));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Deliberately don't call renderBackground: that invokes Minecraft's menu blur.
        float time = ticks / 20.0F;
        graphics.fill(0, 0, width, 30, 0x72000000);
        graphics.drawString(font, Component.translatable("screen.steamcore.awakening.title"),
                14, 12, 0xFFF1E9D7, false);

        String subtitleKey = time < 1.8F ? "awakening.subtitle.dazed"
                : time < 4.35F ? "awakening.subtitle.voice"
                : time < 6.7F ? "awakening.subtitle.hand" : "awakening.subtitle.rise";
        String subtitle = DialogueText.resolve("$" + subtitleKey, minecraft.options.languageCode);
        graphics.fill(0, height - 60, width, height, 0x88000000);
        graphics.drawCenteredString(font, subtitle, width / 2, height - 48, 0xFFF6F3EB);

        float fade = smooth((time - 7.0F) / 1.3F);
        if (fade > 0) graphics.fill(0, 0, width, height, ((int) (fade * 210) << 24) | 0x00FFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private static float smooth(float value) {
        float clamped = Math.max(0.0F, Math.min(1.0F, value));
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || ticks > 15) {
            finish();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticks > 15) {
            finish();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
