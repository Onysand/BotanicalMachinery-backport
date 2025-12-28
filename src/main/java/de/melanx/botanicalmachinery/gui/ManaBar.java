package de.melanx.botanicalmachinery.gui;

import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.GuiScreen;

/*
 * This class is inspired by Cyclics EnergyBar
 */
public class ManaBar {

    private final GuiScreen parent;
    public int x = 153;
    public int y = 15;
    public final int capacity;
    private final int width = 16;
    private final int height = 62;
    public int guiLeft;
    public int guiTop;

    public ManaBar(GuiScreen parent, int capacity) {
        this.parent = parent;
        this.capacity = capacity;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return this.guiLeft + this.x < mouseX && mouseX < this.guiLeft + this.x + this.width
                && this.guiTop + this.y < mouseY && mouseY < this.guiTop + this.y + this.height;
    }

    public void draw(float mana) {
        int relX;
        int relY;
        this.parent.mc.getTextureManager().bindTexture(LibResources.MANA_BAR);
        relX = this.guiLeft + this.x;
        relY = this.guiTop + this.y;
        GuiScreen.drawModalRectWithCustomSizedTexture(relX, relY, 0, 0, this.width, this.height, this.width, this.height);
        this.parent.mc.getTextureManager().bindTexture(LibResources.MANA_BAR_CURRENT);
        relX += 1;
        relY += this.height - 1;
        float pct = Math.min(mana / this.capacity, 1.0F);
        GuiScreen.drawModalRectWithCustomSizedTexture(relX, relY, 0, 0, this.width - 2, (int) -((this.height - 2) * pct), this.width - 2, this.height - 2);
    }

    public void renderHoveredToolTip(int mouseX, int mouseY, int mana) {
        if (this.isMouseOver(mouseX, mouseY) && ClientConfig.numericalMana) {
            this.parent.drawHoveringText(mana + " / " + this.capacity + " Mana", mouseX, mouseY);
        }
    }
}
