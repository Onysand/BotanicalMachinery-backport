package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalDaisy;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class ScreenMechanicalDaisy extends ScreenBase<ContainerMechanicalDaisy> {

    private static final String PURE_DAISY_TEXTURE = "botania:blocks/flower_misc_pure_daisy";

    public ScreenMechanicalDaisy(ContainerMechanicalDaisy container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultGuiBackgroundLayer(LibResources.MECHANICAL_DAISY_GUI, 81, 37);
        this.drawFluidInSlots();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = textureMap.getAtlasSprite(PURE_DAISY_TEXTURE);
        this.drawTexturedModalRect(12, 16, sprite, 48, 48);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }

    @Override
    public void renderHoveredToolTip(int mouseX, int mouseY) {
        if (this.mc.player.inventory.getItemStack().isEmpty() && this.getSlotUnderMouse() != null) {
            if (this.getSlotUnderMouse() instanceof ContainerMechanicalDaisy.ItemAndFluidSlot) {
                ContainerMechanicalDaisy.ItemAndFluidSlot slot = (ContainerMechanicalDaisy.ItemAndFluidSlot) this.getSlotUnderMouse();
                boolean itemEmpty = slot.inventory.getStackInSlot(slot.slotNumber).isEmpty();
                FluidStack fluidStack = slot.inventory.getFluidInTank(this.getSlotUnderMouse().slotNumber);
                if (itemEmpty && fluidStack != null && fluidStack.amount >= 0) {
                    List<String> list = ImmutableList.of(
                        new TextComponentTranslation(fluidStack.getUnlocalizedName()).getFormattedText(),
                        new TextComponentString(fluidStack.amount + " / 1000").getFormattedText()
                    );
                    
                    this.drawHoveringText(list, mouseX, mouseY);
                    return;
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }
    
    private void drawFluidInSlots() {
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        
        this.inventorySlots.inventorySlots.forEach(slot -> {
            if (!(slot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot)) return;
            
            ContainerMechanicalDaisy.ItemAndFluidSlot fs = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot);
            FluidStack stack = fs.inventory.getFluidInTank(fs.slotNumber);
            if (stack == null || stack.amount <= 0) return;
            
            int capacity = fs.inventory.getTankCapacity();
            int height = (int) ((stack.amount / (float) capacity) * 16);
            if (height <= 0) return;
            
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(stack.getFluid().getStill(stack).toString());
            
            int color = stack.getFluid().getColor(stack);
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = ((color >> 24) & 0xFF) / 255f;
            
            if (a <= 0) a = 1f;
            
            GlStateManager.color(r, g, b, a);
            
            int x = this.guiLeft + slot.xPos;
            int y = this.guiTop + slot.yPos + 16 - height;
            
            this.drawTexturedModalRect(x, y, sprite, 16, height);
            
        });
        
        GlStateManager.color(1, 1, 1, 1);
    }
}
