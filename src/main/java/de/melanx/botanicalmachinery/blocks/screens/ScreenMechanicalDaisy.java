package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalDaisy;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class ScreenMechanicalDaisy extends ScreenBase<ContainerMechanicalDaisy> {

    private static final ResourceLocation PURE_DAISY_TEXTURE = new ResourceLocation("botania", "blocks/flower_misc_pure_daisy");

    public ScreenMechanicalDaisy(ContainerMechanicalDaisy container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LibResources.MECHANICAL_DAISY_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(relX, relY, 0, 0, this.xSize, this.ySize);
        
        this.drawFluidInSlots();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.container.tile.getDisplayName().getFormattedText();
        this.fontRenderer.drawString(s, (this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2), 6, 4210752);
        this.fontRenderer.drawString(this.mc.player.inventory.getDisplayName().getFormattedText(), 8, (this.ySize - 96 + 2), 4210752);
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = textureMap.getAtlasSprite(PURE_DAISY_TEXTURE.getResourcePath());
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.drawTexturedModalRect(12, 16, sprite, 48, 48);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        this.renderHoveredToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
    }

    @Override
    public void renderHoveredToolTip(int mouseX, int mouseY) {
        if (this.mc.player.inventory.getItemStack().isEmpty() && this.getSlotUnderMouse() != null) {
            if (this.getSlotUnderMouse() instanceof ContainerMechanicalDaisy.ItemAndFluidSlot
                    && ((ContainerMechanicalDaisy.ItemAndFluidSlot) this.getSlotUnderMouse()).inventory.getStackInSlot(this.getSlotUnderMouse().slotNumber).isEmpty()
                    && !(((ContainerMechanicalDaisy.ItemAndFluidSlot) this.getSlotUnderMouse()).inventory.getFluidInTank(this.getSlotUnderMouse().slotNumber).amount <= 0)) {

                FluidStack stack = ((ContainerMechanicalDaisy.ItemAndFluidSlot) this.getSlotUnderMouse()).inventory.getFluidInTank(this.getSlotUnderMouse().slotNumber);
                List<String> list = ImmutableList.of(
                        new TextComponentTranslation(stack.getUnlocalizedName()).getFormattedText(),
                        new TextComponentString(stack.amount + " / 1000").getFormattedText()
                );

                this.drawHoveringText(list, mouseX, mouseY);
                return;
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }
    
    private void drawFluidInSlots() {
        this.inventorySlots.inventorySlots.forEach(slot -> {
            if (!(slot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot)) return;
            
            ContainerMechanicalDaisy.ItemAndFluidSlot fs = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot);
            FluidStack stack = fs.inventory.getFluidInTank(fs.slotNumber);
            if (stack == null || stack.amount <= 0) return;
            
            int capacity = fs.inventory.getTankCapacity();
            int height = (int) ((stack.amount / (float) capacity) * 16);
            if (height <= 0) return;
            
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(stack.getFluid().getStill(stack).toString());
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            
            int color = stack.getFluid().getColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = ((color >> 24) & 0xFF) / 255f;
            
            GlStateManager.color(r, g, b, a);
            
            int x = this.guiLeft + slot.xPos;
            int y = this.guiTop + slot.yPos + 16 - height;
            
            this.drawTexturedModalRect(x, y, sprite, 16, height);
            
            GlStateManager.color(1, 1, 1, 1);
        });
    }
}
