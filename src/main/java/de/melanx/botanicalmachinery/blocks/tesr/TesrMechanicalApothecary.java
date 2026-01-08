package de.melanx.botanicalmachinery.blocks.tesr;

import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.config.BMConfig;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import vazkii.botania.client.core.handler.ClientTickHandler;

public class TesrMechanicalApothecary extends HorizontalRotatedTesr<TileMechanicalApothecary> {

    public TesrMechanicalApothecary() {
        super();
    }

    @Override
    protected void doRender(TileMechanicalApothecary tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!BMConfig.CLIENT.rendering.all || !BMConfig.CLIENT.rendering.apothecary)
            return;

        if (!tile.getInventory().getStackInSlot(0).isEmpty()) {
            float time = ClientTickHandler.ticksInGame + partialTicks;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, 14.3 / 16, 0.5);
            GlStateManager.scale(6 / 16f, 6 / 16f, 6 / 16f);
            GlStateManager.rotate(time / 1.3f, 0, 1, 0);

            ItemStack stack = tile.getInventory().getStackInSlot(0);

            if (tile.getProgress() > 0) {
                int progress = tile.getProgress();
                if (progress > TileMechanicalApothecary.getRecipeDuration() / 2) {
                    progress = (TileMechanicalApothecary.getRecipeDuration() / 2) - Math.abs((TileMechanicalApothecary.getRecipeDuration() / 2) - progress);
                    stack = tile.getCurrentOutput();
                }
                double amount = progress / (TileMechanicalApothecary.getRecipeDuration() / 2d);
                GlStateManager.translate(0, -amount, 0);
            }

            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

            GlStateManager.popMatrix();
        }

        double fluidAmount = (tile.getFluidInventory().getFluidAmount() - ((tile.getProgress() / (double) TileMechanicalApothecary.getRecipeDuration()) * 1000d)) / (double) tile.getFluidInventory().getCapacity();

        if (tile.getFluidInventory().getFluidAmount() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(4 / 16d, (10 + (fluidAmount * 3.8)) / 16, 4 / 16d);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
            
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            FluidStack fluidStack = tile.getFluidInventory().getFluid();
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill().toString());

            int fluidColor = FluidRegistry.WATER.getColor(tile.getWorld(), tile.getPos());

            RenderHelper.renderIconColored(0, 0, sprite, 8, 8, 1.0F, fluidColor);
            
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }

        int items = 0;
        for (int slot : tile.getInventory().getInputSlots()) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty())
                items += 1;
        }

        double offsetPerPetal = 360d / items;
        double flowerTicks = (double) ((float) ClientTickHandler.ticksInGame + partialTicks) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, (10 + ((fluidAmount * 3.8) / 1.5)) / 16, 0.5);
        GlStateManager.scale(0.125f, 0.125f, 0.125f);

        int nextIdx = 0;
        boolean hasFluid = tile.getFluidInventory().getFluidAmount() > 0;

        for (int slot : tile.getInventory().getInputSlots()) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty()) {
                int i = nextIdx++;

                double offset = offsetPerPetal * i;
                double deg;
                if (hasFluid) {
                    deg = ((flowerTicks / 0.25) % 360) + offset;
                } else {
                    deg = offset;
                }
                double rad = deg * Math.PI / 180;

                double radiusX;
                double radiusZ;
                if (hasFluid) {
                    radiusX = 1.2000000476837158 + 0.10000000149011612 * Math.sin(flowerTicks / 6);
                    radiusZ = 1.2000000476837158 + 0.10000000149011612 * Math.cos(flowerTicks / 6);
                } else {
                    radiusX = 1.2000000476837158 + 0.10000000149011612;
                    radiusZ = 1.2000000476837158 + 0.10000000149011612;
                }

                double rX = radiusX * Math.cos(rad);
                double rZ = radiusZ * Math.sin(rad);
                double rY = hasFluid ? (float) Math.cos((flowerTicks + (double) (50 * i)) / 5.0D) / 10.0F : 0;

                GlStateManager.pushMatrix();
                GlStateManager.translate(rX, rY, rZ);

                GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
                if (hasFluid) {
                    float xRotate = (float) Math.sin(flowerTicks * 0.25) / 2;
                    float yRotate = (float) Math.max(0.6000000238418579, Math.sin(flowerTicks * 0.10000000149011612) / 2 + 0.5);
                    float zRotate = (float) Math.cos(flowerTicks * 0.25) / 2;
                    GlStateManager.rotate((float) deg, xRotate, yRotate, zRotate);
                } else {
                    GlStateManager.rotate(90, 1, 0, 0);
                }
                GlStateManager.translate(-0.0625f, -0.0625f, -0.0625f);

                ItemStack stack = tile.getInventory().getStackInSlot(slot);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }
}
