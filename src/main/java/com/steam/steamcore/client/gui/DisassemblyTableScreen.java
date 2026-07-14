package com.steam.steamcore.client.gui;

import com.steam.steamcore.inventory.DisassemblyTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DisassemblyTableScreen extends AbstractContainerScreen<DisassemblyTableMenu> {

    public DisassemblyTableScreen(DisassemblyTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private Button disassembleButton;

    @Override
    protected void init() {
        super.init();

        this.disassembleButton = Button.builder(Component.literal("Disassemble"), button -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
        }).bounds(this.leftPos + 10, this.topPos + 45, 80, 20).build();

        this.addRenderableWidget(this.disassembleButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.disassembleButton != null) {
            this.disassembleButton.active = this.menu.blockEntity.canDisassemble();
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Main background
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFFC6C6C6);

        // Header section
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + 17, 0xFF8B8B8B);
        guiGraphics.renderOutline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF373737);

        // Input slot
        drawSlot(guiGraphics, this.leftPos + 120, this.topPos + 20);

        // Output slots
        int outputStartX = this.leftPos + 102;
        int outputStartY = this.topPos + 45;
        guiGraphics.fill(outputStartX - 5, outputStartY - 5, outputStartX + 59, outputStartY + 41, 0xFF8B8B8B);
        guiGraphics.renderOutline(outputStartX - 5, outputStartY - 5, 64, 46, 0xFF545454);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                drawSlot(guiGraphics, outputStartX + j * 18, outputStartY + i * 18);
            }
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        // Lighter slot background
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF8B8B8B);
        graphics.renderOutline(x - 1, y - 1, 18, 18, 0xFF545454);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
