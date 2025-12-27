package de.melanx.botanicalmachinery.blocks.screens;

import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBattery;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import de.melanx.botanicalmachinery.core.LibResources;
import de.melanx.botanicalmachinery.helper.SoundHelper;
import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class ScreenManaBattery extends ScreenBase<ContainerManaBattery> {
    private int xB1;
    private int yB1;
    private int xB2;
    private int yB2;

    public ScreenManaBattery(ContainerManaBattery container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.xB1 = this.relX + 51;
        this.yB1 = this.relY + 49;
        this.xB2 = this.relX + 105;
        this.yB2 = this.relY + 49;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MANA_BATTERY_GUI, 81, 37);

        BlockPos tilePos = this.container.getPos();
        TileManaBattery tile = (TileManaBattery) this.container.getWorld().getTileEntity(tilePos);
        if (tile == null) return;

        this.mc.getTextureManager().bindTexture(LibResources.MANA_BATTERY_GUI);
        if (mouseX >= this.xB1 && mouseX < this.xB1 + 20 && mouseY >= this.yB1 && mouseY < this.yB1 + 20) {
            this.drawTexturedModalRect(this.xB1, this.yB1, 20, tile.isSlot1Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        } else {
            this.drawTexturedModalRect(this.xB1, this.yB1, 0, tile.isSlot1Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        }

        if (mouseX >= this.xB2 && mouseX < this.xB2 + 20 && mouseY >= this.yB2 && mouseY < this.yB2 + 20) {
            this.drawTexturedModalRect(this.xB2, this.yB2, 20, tile.isSlot2Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        } else {
            this.drawTexturedModalRect(this.xB2, this.yB2, 0, tile.isSlot2Locked() ? this.ySize + 20 : this.ySize, 20, 20);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        if (clickedButton == 0) {
            BlockPos tilePos = this.container.getPos();
            TileManaBattery tile = (TileManaBattery) this.container.getWorld().getTileEntity(tilePos);
            if (tile == null) super.mouseClicked(mouseX, mouseY, clickedButton);
            if (mouseX >= this.xB1 && mouseX < this.xB1 + 20 && mouseY >= this.yB1 && mouseY < this.yB1 + 20) {
                tile.setSlot1Locked(!tile.isSlot1Locked());
                BotanicalMachineryNetwork.updateLockedState(tile);
                SoundHelper.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
            if (mouseX >= this.xB2 && mouseX < this.xB2 + 20 && mouseY >= this.yB2 && mouseY < this.yB2 + 20) {
                tile.setSlot2Locked(!tile.isSlot2Locked());
                BotanicalMachineryNetwork.updateLockedState(tile);
                SoundHelper.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
        }
        super.mouseClicked(mouseX, mouseY, clickedButton);
    }
}
