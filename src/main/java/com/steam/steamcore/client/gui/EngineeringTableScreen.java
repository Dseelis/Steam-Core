package com.steam.steamcore.client.gui;

import com.steam.steamcore.inventory.EngineeringTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EngineeringTableScreen extends AbstractContainerScreen<EngineeringTableMenu> {

    public EngineeringTableScreen(EngineeringTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private Button attachButton;
    private Button detachButton;

    @Override
    protected void init() {
        super.init();

        this.attachButton = Button.builder(Component.translatable("gui.steamcore.engineering_table.attach"), button -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
        }).bounds(this.leftPos + 30, this.topPos + 60, 120, 20).build();

        this.detachButton = Button.builder(Component.translatable("gui.steamcore.engineering_table.detach"), button -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
            }
        }).bounds(this.leftPos + 30, this.topPos + 60, 120, 20).build();

        this.addRenderableWidget(this.attachButton);
        this.addRenderableWidget(this.detachButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.attachButton != null && this.detachButton != null) {
            boolean canAttach = this.menu.canAttach();
            boolean canDetach = this.menu.canDetach();

            this.attachButton.active = canAttach;
            this.attachButton.visible = !canDetach;

            this.detachButton.active = canDetach;
            this.detachButton.visible = canDetach;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Main background - lighter, more modern color
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFFC6C6C6);

        // Header section
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + 17, 0xFF8B8B8B);
        guiGraphics.renderOutline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF373737);

        // Input slots container
        int inputStartX = this.leftPos + 30;
        int inputStartY = this.topPos + 30;
        guiGraphics.fill(inputStartX - 5, inputStartY - 5, inputStartX + 73, inputStartY + 27, 0xFF8B8B8B);
        guiGraphics.renderOutline(inputStartX - 5, inputStartY - 5, 78, 32, 0xFF545454);

        // Helmet slot
        drawSlot(guiGraphics, inputStartX, inputStartY + 5);

        // Goggles slot
        drawSlot(guiGraphics, inputStartX + 50, inputStartY + 5);

        // Output slot container
        int outputX = this.leftPos + 134;
        int outputY = this.topPos + 35;
        guiGraphics.fill(outputX - 5, outputY - 5, outputX + 22, outputY + 22, 0xFF8B8B8B);
        guiGraphics.renderOutline(outputX - 5, outputY - 5, 27, 27, 0xFF545454);

        // Output slot
        drawSlot(guiGraphics, outputX, outputY);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        // Lighter slot background with subtle shadow
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
