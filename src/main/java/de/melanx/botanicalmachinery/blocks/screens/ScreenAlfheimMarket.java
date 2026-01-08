package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerAlfheimMarket;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import de.melanx.botanicalmachinery.core.LibResources;

public class ScreenAlfheimMarket extends ScreenBase<ContainerAlfheimMarket> {
    public ScreenAlfheimMarket(ContainerAlfheimMarket container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.ALFHEIM_MARKET_GUI, 3, 57);
        TileAlfheimMarket tile = (TileAlfheimMarket) this.container.tile;
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / (float) tile.getMaxProgress(), 1.0F);
            this.mc.getTextureManager().bindTexture(LibResources.ALFHEIM_MARKET_GUI);
            this.drawTexturedModalRect(this.relX + 77, this.relY + 35, 176, 0, Math.round(22 * pct), 16);
        }
    }
}
