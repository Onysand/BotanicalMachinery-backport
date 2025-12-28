package de.melanx.botanicalmachinery.blocks.tesr;

import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.config.ClientConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.model.ModelSpinningCubes;

public class TesrMechanicalRunicAltar extends HorizontalRotatedTesr<TileMechanicalRunicAltar> {

    private final ModelSpinningCubes modelCubes = new ModelSpinningCubes();
    private final ModelRenderer spinningCube = new ModelRenderer(modelCubes, 42, 0)
        .addBox(-4, -4, -4, 8, 8, 8);

    public TesrMechanicalRunicAltar() {
        super();
    }

    @Override
    protected void doRender(TileMechanicalRunicAltar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!ClientConfig.everything || !ClientConfig.runicAltar)
            return;
        
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        ItemStack livingRockStack = tile.getInventory().getStackInSlot(0);
        if (!livingRockStack.isEmpty() && livingRockStack.getItem() instanceof ItemBlock) {
            IBlockState state = ((ItemBlock) livingRockStack.getItem()).getBlock().getDefaultState();

            GlStateManager.pushMatrix();
            GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
            GlStateManager.translate(6.5, 10, 6.5);
            GlStateManager.scale(3, 3, 3);

            GlStateManager.translate(0.5, 0, 0.5);
            GlStateManager.rotate(-(ClientTickHandler.ticksInGame + partialTicks), 0, 1, 0);
            GlStateManager.translate(-0.5, 0, -0.5);

            //noinspection deprecation
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(state, tile.getPos(), tile.getWorld(), buffer);

            GlStateManager.popMatrix();
        }

        double progressLeft = 1 - ((tile.getProgress() / (double) tile.getMaxProgress()) * 0.9);

        int items = 0;
        for (int slot : tile.getInventory().getInputSlots()) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty())
                items += 1;
        }

        float[] angles = new float[items];
        float anglePer = 360f / items;
        float totalAngle = 0;
        for (int i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }

        float time = ClientTickHandler.ticksInGame + partialTicks;

        int nextAngleIdx = 0;
        for (int slot : tile.getInventory().getInputSlots()) {
            if (!tile.getInventory().getStackInSlot(slot).isEmpty()) {
                double travelCenter = 1;
                boolean shrink = false;
                if (tile.isSlotUsedCurrently(slot)) {
                    travelCenter = progressLeft;
                } else if (tile.getProgress() > 0) {
                    shrink = true;
                }

                int angleIdx = nextAngleIdx++;
                if (angleIdx >= angles.length)
                    break;

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5, 10.8 / 16d, 0.5);
                GlStateManager.scale(0.3f, 0.3f, 0.3f);
                GlStateManager.rotate(angles[angleIdx] + time, 0, 1, 0);
                GlStateManager.translate(travelCenter * 1.125, 0, travelCenter * 0.25);
                GlStateManager.rotate(90f, 0, 1, 0);
                GlStateManager.translate(0, 0.075 * Math.sin((time + (angleIdx * 10)) / 5d), 0);
                if (shrink)
                    GlStateManager.scale(0.3f, 0.3f, 0.3f);

                ItemStack stack = tile.getInventory().getStackInSlot(slot);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

                GlStateManager.popMatrix();
            }
        }
    }
}
