package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalRunicAltar;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.block.ModBlocks;

public class ScreenMechanicalRunicAltar extends ScreenBase<ContainerMechanicalRunicAltar> {
    public ScreenMechanicalRunicAltar(ContainerMechanicalRunicAltar container) {
        super(container);
        this.xSize = 216;
        this.ySize = 195;
        this.manaBar.x += 40;
        this.manaBar.y += 20;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_RUNIC_ALTAR_GUI, 91, 65);
        RenderHelper.renderFadedItem(this, new ItemStack(ModBlocks.livingrock), this.guiLeft + 90, this.guiTop + 43);
        TileMechanicalRunicAltar tile = (TileMechanicalRunicAltar) this.container.tile;
        if (tile.getProgress() > 0) {
            float pct = Math.min(tile.getProgress() / (float) tile.getMaxProgress(), 1.0F);
            this.mc.getTextureManager().bindTexture(LibResources.MECHANICAL_RUNIC_ALTAR_GUI);
            this.drawTexturedModalRect(this.guiLeft + 87, this.guiTop + 64, this.xSize, 0, Math.round(22 * pct), 16);
        }
    }
}
