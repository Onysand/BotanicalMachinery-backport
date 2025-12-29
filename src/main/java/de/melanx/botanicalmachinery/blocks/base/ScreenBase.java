package de.melanx.botanicalmachinery.blocks.base;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.gui.ManaBar;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class ScreenBase<X extends ContainerBase<?>> extends GuiContainer {
    public final ManaBar manaBar;
    public int relX;
    public int relY;
    public final ContainerBase<?> container;
    
    public ScreenBase(X container) {
        super(container);
        this.manaBar = new ManaBar(this, ((IManaMachineTile) container.tile).getManaCap());
        this.container = container;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.relX = (this.getXSize() - this.xSize) / 2;
        this.relY = (this.getYSize() - this.ySize) / 2;
    }
    
    @Override
    public void renderHoveredToolTip(int mouseX, int mouseY) {
        this.manaBar.guiTop = this.guiTop;
        this.manaBar.guiLeft = this.guiLeft;
        this.drawDefaultBackground();
        super.renderHoveredToolTip(mouseX, mouseY);
        this.manaBar.renderHoveredToolTip(mouseX, mouseY, ((TileBase) this.container.tile).getCurrentMana());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.container.tile.getDisplayName().getFormattedText();
        this.fontRenderer.drawString(s, (this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2), 6, Color.DARK_GRAY.getRGB());
        this.fontRenderer.drawString(this.mc.player.inventory.getDisplayName().getFormattedText(), 6, (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());
    }

    public void drawDefaultGuiBackgroundLayer(ResourceLocation screenLocation) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(screenLocation);
        this.drawTexturedModalRect(this.relX, this.relY, 0, 0, this.xSize, this.ySize);
        this.manaBar.draw(((TileBase) this.container.tile).getCurrentMana());
    }

    public void drawDefaultGuiBackgroundLayer(ResourceLocation screenLocation, int crossX, int crossY) {
        this.drawDefaultGuiBackgroundLayer(screenLocation);

        BlockPos tilePos = this.container.getPos();
        TileEntity tile = this.container.getWorld().getTileEntity(tilePos);
        if (tile instanceof TileBase && !((TileBase) tile).hasValidRecipe()) {
            int x = this.relX + crossX;
            int y = this.relY + crossY;

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.mc.getTextureManager().bindTexture(LibResources.HUD);
            this.drawTexturedModalRect(x, y, 0, 0, 13, 13);

            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
        }
    }
}
