package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;

public class ScreenMechanicalApothecary extends ScreenBase<ContainerMechanicalApothecary> {
    private final int relX;
    private final int relY;
    private final TileMechanicalApothecary tile;
    private final static ResourceLocation water = new ResourceLocation("block/water_still");

    public ScreenMechanicalApothecary(ContainerMechanicalApothecary screenContainer) {
        super(screenContainer);
        this.xSize = 196;
        this.ySize = 195;
        this.relX = (this.width - this.xSize) / 2;
        this.relY = (this.height - this.ySize) / 2;
        this.tile = screenContainer.tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_APOTHECARY_GUI, 81, 37);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LibResources.MECHANICAL_APOTHECARY_GUI);

        this.drawTexturedModalRect(this.relX, this.relY, 0, 0, this.xSize, this.ySize);

        if (this.tile.getInventory().getStackInSlot(0).isEmpty())
            RenderHelper.renderFadedItem(this, ImmutableList.copyOf(OreDictionary.getOres("seed")), this.relX + 90, this.relY + 43);

        if (this.tile.getProgress() > 0) {
            float pctProgress = Math.min(this.tile.getProgress() / (float) TileMechanicalApothecary.getRecipeDuration(), 1.0F);
            this.mc.getTextureManager().bindTexture(LibResources.MECHANICAL_APOTHECARY_GUI);
            this.drawTexturedModalRect(this.relX + 87, this.relY + 64, this.xSize, 0, Math.round(22 * pctProgress), 16);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.tile.getDisplayName().getFormattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, Color.DARK_GRAY.getRGB());
        this.fontRenderer.drawString(this.mc.player.inventory.getDisplayName().getFormattedText(), 8, (this.ySize - 96 + 2), Color.DARK_GRAY.getRGB());

        float pctFluid = Math.min((float) this.tile.getFluidInventory().getFluidAmount() / TileMechanicalApothecary.FLUID_CAPACITY, 1.0F);
        this.mc.getTextureManager().bindTexture(water);
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = textureMap.getAtlasSprite(water.toString());
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int fluidColor = FluidRegistry.WATER.getColor();
        float fluidColorA = ((fluidColor >> 24) & 0xFF) / 255f;
        float fluidColorR = ((fluidColor >> 16) & 0xFF) / 255f;
        float fluidColorG = ((fluidColor >> 8) & 0xFF) / 255f;
        float fluidColorB = ((fluidColor) & 0xFF) / 255f;
        GlStateManager.color(fluidColorR, fluidColorG, fluidColorB, fluidColorA);
        int xPos = 163;
        int ySize = Math.round(81 * pctFluid);
        int yPos = 16 + 81 - ySize;
        RenderHelper.repeatBlit(xPos, yPos, 16, 16, 17, ySize, sprite);
        GlStateManager.color(1, 1, 1, 1);

        this.mc.getTextureManager().bindTexture(LibResources.MECHANICAL_APOTHECARY_GUI);
        this.drawTexturedModalRect(xPos, 16, this.xSize, 16, 17, 81);

        this.renderHoveredToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
    }

    @Override
    public void renderHoveredToolTip(int mouseX, int mouseY) {
        if (mouseX >= 163 && mouseX <= 179 &&
                mouseY >= 16 && mouseY <= 96) {
            String fluid = this.tile.getFluidInventory().getFluidAmount() + " / " + this.tile.getFluidInventory().getCapacity() + " mB";
            this.drawHoveringText(fluid, mouseX, mouseY);
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }
}
