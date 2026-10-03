package com.steam.steamcore.client.gui;

import com.steam.steamcore.inventory.EternalInfuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EternalInfuserScreen extends AbstractContainerScreen<EternalInfuserMenu> {

    public EternalInfuserScreen(EternalInfuserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        SteamCoreGuiStyle.drawFrame(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        // Draw slots
        drawSlot(guiGraphics, this.leftPos + 55, this.topPos + 34);
        drawSlot(guiGraphics, this.leftPos + 115, this.topPos + 34);

        // Recessed brass-trimmed energy gauge
        guiGraphics.fill(this.leftPos + 79, this.topPos + 19, this.leftPos + 91, this.topPos + 71, SteamCoreGuiStyle.BORDER);
        guiGraphics.fill(this.leftPos + 80, this.topPos + 20, this.leftPos + 90, this.topPos + 70, SteamCoreGuiStyle.SLOT);
        
        // Energy fill
        float ratio = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
        int height = (int) (50 * ratio);
        guiGraphics.fill(this.leftPos + 81, this.topPos + 69 - height, this.leftPos + 89, this.topPos + 69, SteamCoreGuiStyle.BRASS);
        if (height > 1) {
            guiGraphics.fill(this.leftPos + 82, this.topPos + 69 - height, this.leftPos + 83, this.topPos + 69, 0xFFFFE0A0);
        }
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
        
        // Show energy text on hover
        if (mouseX >= this.leftPos + 80 && mouseX <= this.leftPos + 90 && mouseY >= this.topPos + 20 && mouseY <= this.topPos + 70) {
            guiGraphics.renderTooltip(this.font, Component.literal(this.menu.getEnergy() + " / " + this.menu.getMaxEnergy() + " FE"), mouseX, mouseY);
        }
    }
}
