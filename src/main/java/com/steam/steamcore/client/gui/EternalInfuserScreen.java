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
        // Draw background
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF333333);
        guiGraphics.renderOutline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFFAAAAAA);

        // Draw slots
        drawSlot(guiGraphics, this.leftPos + 55, this.topPos + 34);
        drawSlot(guiGraphics, this.leftPos + 115, this.topPos + 34);

        // Energy bar background
        guiGraphics.fill(this.leftPos + 80, this.topPos + 20, this.leftPos + 90, this.topPos + 70, 0xFF000000);
        
        // Energy fill
        float ratio = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
        int height = (int) (50 * ratio);
        guiGraphics.fill(this.leftPos + 81, this.topPos + 69 - height, this.leftPos + 89, this.topPos + 69, 0xFFFF0000);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF111111);
        graphics.renderOutline(x - 1, y - 1, 18, 18, 0xFF8B8B8B);
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
