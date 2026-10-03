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
        this.imageHeight = 186;
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
            this.attachButton.visible = canAttach || !canDetach;

            this.detachButton.active = canDetach;
            this.detachButton.visible = canDetach && !canAttach;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        SteamCoreGuiStyle.drawFrame(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        // Helmet, Create goggles, and spyglass input slots
        int inputStartX = this.leftPos + 17;
        int inputStartY = this.topPos + 30;
        SteamCoreGuiStyle.drawPanel(guiGraphics, inputStartX, inputStartY, 108, 28);

        drawSlot(guiGraphics, inputStartX + 5, inputStartY + 5);
        drawSlot(guiGraphics, inputStartX + 39, inputStartY + 5);
        drawSlot(guiGraphics, inputStartX + 73, inputStartY + 5);

        // Output slot container
        int outputX = this.leftPos + 134;
        int outputY = this.topPos + 35;
        SteamCoreGuiStyle.drawPanel(guiGraphics, outputX - 5, outputY - 5, 27, 27);

        // Output slot
        drawSlot(guiGraphics, outputX, outputY);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        SteamCoreGuiStyle.drawSlot(graphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, SteamCoreGuiStyle.TEXT, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, SteamCoreGuiStyle.TEXT_MUTED, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
