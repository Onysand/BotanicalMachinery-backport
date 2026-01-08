package de.melanx.botanicalmachinery.blocks.tesr;

import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileAlfheimMarket;
import de.melanx.botanicalmachinery.config.BMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.common.block.ModBlocks;

public class TesrAlfheimMarket extends HorizontalRotatedTesr<TileAlfheimMarket> {

    public TesrAlfheimMarket() {
        super();
    }
    
    @Override
    public void doRender(TileAlfheimMarket tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!BMConfig.CLIENT.rendering.all || !BMConfig.CLIENT.rendering.alfheimMarket)
            return;
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
        GlStateManager.translate(3.2, 2, 3.6);
        GlStateManager.scale(3.6f, 3.6f, 3.6f);
        
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(ModBlocks.pylon.getDefaultState(), tile.getPos(), tile.getWorld(), buffer);
        GlStateManager.translate(1 + (2 / 3.6), 0, 0);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(ModBlocks.pylon.getDefaultState(), tile.getPos(), tile.getWorld(), buffer);
        tessellator.draw();
        
        GlStateManager.popMatrix();
        
        if (tile.getCurrentMana() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
            GlStateManager.translate(6.8, 1, 8.8);
            GlStateManager.scale(2.4f, 2.4f, 2.4f);
            
            GlStateManager.translate(-1.0D, 1.0D, 0.25D);
            
            GlStateManager.disableCull();
            
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            
            float renderAlpha = (float) Math.min(1.0D, (Math.sin((double) ((float) ClientTickHandler.ticksInGame + partialTicks) / 8.0D) + 1.0D) / 7.0D + 0.6D);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            this.renderPortal(buffer, MiscellaneousIcons.INSTANCE.alfPortalTex, 0, 0, 3, 3, renderAlpha);
            tessellator.draw();
            
            GlStateManager.translate(0.0D, 0.0D, 0.5D);
            
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            this.renderPortal(buffer, MiscellaneousIcons.INSTANCE.alfPortalTex, 0, 0, 3, 3, renderAlpha);
            tessellator.draw();

            GlStateManager.enableCull();
            
            GlStateManager.popMatrix();
        }

        if (tile.getProgress() > 0) {
            double progress = (tile.getProgress() - (tile.getMaxProgress() / 2d)) / (tile.getMaxProgress() / 2d);
            ItemStack stack = progress < 0 ? tile.getCurrentInput() : tile.getCurrentOutput();
            if (!stack.isEmpty()) {
                double yPos = Math.pow(progress, 2);
                double zPos = -(progress * 0.75);

                GlStateManager.pushMatrix();
                GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
                GlStateManager.translate(8, 4.6, 8.8);
                GlStateManager.scale(5.4f, 5.4f, 5.4f);
                GlStateManager.translate(0, yPos, zPos);
                
//                RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
//                GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//                GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderPortal(BufferBuilder buffer, TextureAtlasSprite sprite, int x, int y, int width, int height, float alpha) {
        buffer.pos(x, y + height, 0.0F)
            .tex(sprite.getMinU(), sprite.getMaxV())
            .color(1.0F, 1.0F, 1.0F, alpha)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
        buffer.pos(x + width, y + height, 0.0F)
            .tex(sprite.getMaxU(), sprite.getMaxV())
            .color(1.0F, 1.0F, 1.0F, alpha)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
        buffer.pos(x + width, y, 0.0F)
            .tex(sprite.getMaxU(), sprite.getMinV())
            .color(1.0F, 1.0F, 1.0F, alpha)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
        buffer.pos(x, y, 0.0F)
            .tex(sprite.getMinU(), sprite.getMinV())
            .color(1.0F, 1.0F, 1.0F, alpha)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
    }
}
