package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalManaPool;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class ScreenMechanicalManaPool extends ScreenBase<ContainerMechanicalManaPool> {
    public ScreenMechanicalManaPool(ContainerMechanicalManaPool container) {
        super(container);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_MANA_POOL_GUI, 81, 37);
        RenderHelper.renderFadedItem(this, new ArrayList<>(TileMechanicalManaPool.CATALYSTS), this.guiLeft + 53, this.guiTop + 47);
    }
}
