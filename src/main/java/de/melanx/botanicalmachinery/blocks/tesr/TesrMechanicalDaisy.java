package de.melanx.botanicalmachinery.blocks.tesr;

import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import de.melanx.botanicalmachinery.config.ClientConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TesrMechanicalDaisy extends TileEntitySpecialRenderer<TileMechanicalDaisy> {

    private static final float SCALE = 0.3125f;
    
    @Override
    public void render(@Nonnull TileMechanicalDaisy tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!ClientConfig.everything || !ClientConfig.daisy)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(SCALE, SCALE, SCALE);
        GlStateManager.translate(0, 0.125f / SCALE, 0);
        this.renderState(tile.getState(0), 0 / SCALE, 0 / SCALE);
        this.renderState(tile.getState(1), 0.34375f / SCALE, 0 / SCALE);
        this.renderState(tile.getState(2), 0.6875f / SCALE, 0 / SCALE);
        this.renderState(tile.getState(3), 0 / SCALE, 0.34375f / SCALE);
        this.renderState(tile.getState(4), 0.6875f / SCALE, 0.34375f / SCALE);
        this.renderState(tile.getState(5), 0 / SCALE, 0.6875f / SCALE);
        this.renderState(tile.getState(6), 0.34375f / SCALE, 0.6875f / SCALE);
        this.renderState(tile.getState(7), 0.6875f / SCALE, 0.6875f / SCALE);
        GlStateManager.popMatrix();
        
        GlStateManager.popMatrix();
    }

    private void renderState(@Nullable IBlockState state, float translateX, float translateZ) {
        if (state != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(translateX, 0, translateZ);
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1F);
            GlStateManager.popMatrix();
        }
    }
}
