package de.melanx.botanicalmachinery.gui;

import de.melanx.botanicalmachinery.config.BMConfig;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.gui.GuiScreen;

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
        this.parent.mc.getTextureManager().bindTexture(LibResources.MANA_BAR);
        int relX = this.guiLeft + this.x;
        int relY = this.guiTop + this.y;
        GuiScreen.drawModalRectWithCustomSizedTexture(relX, relY, 0, 0, this.width, this.height, this.width, this.height);
        
        float pct = Math.min(mana / this.capacity, 1.0F);
        int manaHeight = (int) ((this.height - 2) * pct);
        if (manaHeight > 0) {
            this.parent.mc.getTextureManager().bindTexture(LibResources.MANA_BAR_CURRENT);
            int renderY = relY + (this.height - 1) - manaHeight;
            GuiScreen.drawModalRectWithCustomSizedTexture(relX + 1, renderY, 0, 0, this.width - 2, manaHeight, this.width - 2, this.height - 2);
        }
    }

    public void renderHoveredToolTip(int mouseX, int mouseY, int mana) {
        if (this.isMouseOver(mouseX, mouseY) && BMConfig.CLIENT.numericalMana) {
            this.parent.drawHoveringText(mana + " / " + this.capacity + " Mana", mouseX, mouseY);
        }
    }
}
