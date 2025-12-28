package de.melanx.botanicalmachinery.blocks.tesr;

import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.IconHelper;

public class TesrIndustrialAgglomerationFactory extends HorizontalRotatedTesr<TileIndustrialAgglomerationFactory> {

    public TesrIndustrialAgglomerationFactory() {
        super();
    }

    @Override
    protected void doRender(TileIndustrialAgglomerationFactory tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!ClientConfig.everything || !ClientConfig.agglomerationFactory)
            return;

        double progressLeft = 1 - (tile.getProgress() / (double) tile.getMaxProgress());

        this.renderStack(tile.getInventory().getStackInSlot(0), partialTicks, progressLeft, 0);
        this.renderStack(tile.getInventory().getStackInSlot(1), partialTicks, progressLeft, 120);
        this.renderStack(tile.getInventory().getStackInSlot(2), partialTicks, progressLeft, 240);

        if (!tile.getInventory().getStackInSlot(3).isEmpty()) {
            float time = ClientTickHandler.ticksInGame + partialTicks;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, 11.2 / 16d, 0.5);
            GlStateManager.scale(0.3f, 0.3f, 0.3f);
            GlStateManager.rotate(-time, 0, 1, 0);
            GlStateManager.translate(0, 0.075 * Math.sin(time / 5d), 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(tile.getInventory().getStackInSlot(3), ItemCameraTransforms.TransformType.GROUND);

            GlStateManager.popMatrix();
        }

        if (tile.getProgress() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(6.2 / 16, 4.7 / 16, 6.2 / 16);
            GlStateManager.scale(3.6f / 16, 3.6f / 16, 3.6f / 16);

            float alphaMod = 50000 * (tile.getProgress() / (float) tile.getMaxProgress());
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.translate(0, 0, -0.18850000202655792);
            float renderAlpha = (float) ((Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 8) + 1) / 5 + 0.6) * alphaMod;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, renderAlpha);
            
            IconHelper.renderIconIn3D(Tessellator.getInstance(),
                MiscellaneousIcons.INSTANCE.terraPlateOverlay.getMinU(),
                MiscellaneousIcons.INSTANCE.terraPlateOverlay.getMinV(),
                MiscellaneousIcons.INSTANCE.terraPlateOverlay.getMaxU(),
                MiscellaneousIcons.INSTANCE.terraPlateOverlay.getMaxV(),
                1, 1, 1.0F/16.0F);
            
            GlStateManager.popMatrix();
        }
    }

    private void renderStack(ItemStack stack, float partialTicks, double progressLeft, float angle) {
        if (!stack.isEmpty()) {
            double progressLeftScaled = progressLeft * 0.95;
            float time = ClientTickHandler.ticksInGame + partialTicks;
            float colorTint = (float) Math.min(1, progressLeft * 1.2);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, (10.4 - (4 * progressLeft)) / 16d, 0.5);
            GlStateManager.scale(0.3f, 0.3f, 0.3f);
            GlStateManager.rotate(angle + time, 0, 1, 0);
            GlStateManager.translate(progressLeftScaled * 1.125, 0, progressLeftScaled * 0.25);
            GlStateManager.rotate(90f, 0, 1, 0);
            GlStateManager.translate(0, 0.075 * Math.sin((time + angle) / 5d), 0);

            RenderHelper.renderItemTinted(stack, ItemCameraTransforms.TransformType.GROUND, colorTint, 1, colorTint);

            GlStateManager.popMatrix();
        }
    }
}
