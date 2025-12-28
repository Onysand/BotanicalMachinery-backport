package de.melanx.botanicalmachinery.blocks.base;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public abstract class HorizontalRotatedTesr<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    protected TileEntityRendererDispatcher rendererDispatcher = TileEntityRendererDispatcher.instance;
    
    public HorizontalRotatedTesr() {
        super();
    }
    
    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        EnumFacing facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockHorizontal.FACING);
        float f = facing.getHorizontalAngle() + 180;
        GlStateManager.translate(0.5D, 0.5D, 0.5D);
        GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5D, -0.5D, -0.5D);
        this.doRender(tile, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.popMatrix();
    }
    
    protected abstract void doRender(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha);
}
