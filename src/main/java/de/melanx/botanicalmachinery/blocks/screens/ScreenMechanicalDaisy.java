package de.melanx.botanicalmachinery.blocks.screens;

import com.google.common.collect.ImmutableList;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalDaisy;
import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class ScreenMechanicalDaisy extends ScreenBase<ContainerMechanicalDaisy> {

    private static final ResourceLocation PURE_DAISY_TEXTURE = new ResourceLocation("botania", "blocks/flower_misc_pure_daisy");

    public ScreenMechanicalDaisy(ContainerMechanicalDaisy container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        //noinspection deprecation
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.mc.getTextureManager().bindTexture(LibResources.MECHANICAL_DAISY_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(relX, relY, 0, 0, this.xSize, this.ySize);
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
        TextureAtlasSprite sprite = textureMap.getAtlasSprite(PURE_DAISY_TEXTURE.getPath());
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.drawTexturedModalRect(12, 16, sprite, 48, 48);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        this.renderHoveredToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
    }
    
//    @Override
    protected void drawSlot(@Nonnull Slot slot) {
        if (slot instanceof ContainerMechanicalDaisy.ItemAndFluidSlot) {
            FluidStack stack = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getFluidInTank(slot.slotNumber);
            if (stack.getFluid() != null && stack.amount > 0) {
                int maxAmount = ((ContainerMechanicalDaisy.ItemAndFluidSlot) slot).inventory.getTankCapacity(slot.slotNumber);
                int yHeight = Math.round(stack.amount / (float) maxAmount) * 16;
                int yPos = slot.yPos + 16 - yHeight;

                ResourceLocation still = stack.getFluid().getStill(stack);
                TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
                TextureAtlasSprite sprite = textureMap.getAtlasSprite(still.getPath());
                this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                int fluidColor = stack.getFluid().getColor(stack);
                float fluidColorA = ((fluidColor >> 24) & 0xFF) / 255f;
                float fluidColorR = ((fluidColor >> 16) & 0xFF) / 255f;
                float fluidColorG = ((fluidColor >> 8) & 0xFF) / 255f;
                float fluidColorB = ((fluidColor) & 0xFF) / 255f;
                GlStateManager.color(fluidColorR, fluidColorG, fluidColorB, fluidColorA);
                this.drawTexturedModalRect(slot.xPos, yPos, sprite, 16, yHeight);
                GlStateManager.color(1, 1, 1, 1);
            }
        }
//        super.drawSlot(slot);
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
}
