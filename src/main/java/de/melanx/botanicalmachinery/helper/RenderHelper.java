package de.melanx.botanicalmachinery.helper;

import de.melanx.botanicalmachinery.core.LibResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RenderHelper {
    
    public static void renderFadedItem(GuiContainer screen, ArrayList<Item> items, int x, int y) {
        renderFadedItem(screen, items.stream().map(ItemStack::new).collect(Collectors.toList()), x, y);
    }
    
    public static void renderFadedItem(GuiContainer screen, List<ItemStack> items, int x, int y) {
        if (items.isEmpty()) items = Collections.singletonList(new ItemStack(Items.AIR));
        int idx = (items.size() + ((ClientTickHandler.ticksInGame / 20) % items.size())) % items.size();
        renderFadedItem(screen, items.get(idx), x, y);
    }
    
    public static void renderFadedItem(GuiContainer screen, Item item, int x, int y) {
        renderFadedItem(screen, new ItemStack(item), x, y);
    }
    
    public static void renderFadedItem(GuiContainer screen, ItemStack stack, int x, int y) {
        screen.mc.getRenderItem().renderItemIntoGUI(stack, x, y);
        
        GlStateManager.disableDepth();
        screen.mc.getTextureManager().bindTexture(LibResources.HUD);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        vazkii.botania.client.core.helper.RenderHelper.drawTexturedModalRect(x, y, 1.0F, 16, 0, 16, 16);
        GlStateManager.enableDepth();
    }

    /**
     * Repeatedly blits a texture
     *
     * @param x             x coordinate of topleft corner
     * @param y             y coordinate of topleft corner
     * @param texWidth      width of one texture element. If this is lower than displayWidth the texture is looped.
     * @param texHeight     height of one texture element. If this is lower than displayHeight the texture is looped.
     * @param displayWidth  the width of the blit
     * @param displayHeight the height of the blit
     * @param sprite        A texture sprite
     */
    public static void repeatBlit(int x, int y, int texWidth, int texHeight, int displayWidth, int displayHeight, TextureAtlasSprite sprite) {
        repeatBlit(x, y, texWidth, texHeight, displayWidth, displayHeight, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public static void repeatBlit(int x, int y, int texWidth, int texHeight, int displayWidth, int displayHeight, float minU, float maxU, float minV, float maxV) {
        int pixelsRenderedX = 0;
        while (pixelsRenderedX < displayWidth) {
            int pixelsNowX = Math.min(texWidth, displayWidth - pixelsRenderedX);
            float maxUnow = maxU;
            if (pixelsNowX < texWidth) {
                maxUnow = minU + ((maxU - minU) * (pixelsNowX / (float) texWidth));
            }

            int pixelsRenderedY = 0;
            while (pixelsRenderedY < displayHeight) {
                int pixelsNowY = Math.min(texHeight, displayHeight - pixelsRenderedY);
                float maxVnow = maxV;
                if (pixelsNowY < texHeight) {
                    maxVnow = minV + ((maxV - minV) * (pixelsNowY / (float) texHeight));
                }
                
                drawInnerBlit(x + pixelsRenderedX, x + pixelsRenderedX + pixelsNowX,
                        y + pixelsRenderedY, y + pixelsRenderedY + pixelsNowY,
                        0, minU, maxUnow, minV, maxVnow);

                pixelsRenderedY += pixelsNowY;
            }
            pixelsRenderedX += pixelsNowX;
        }
    }
    
    public static void renderItemTinted(ItemStack stack, ItemCameraTransforms.TransformType transformType, float r, float g, float b) {
        if (stack.isEmpty()) return;
        
        GlStateManager.pushMatrix();
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
        model = ForgeHooksClient.handleCameraTransforms(model, transformType, false);
        
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        GlStateManager.color(r, g, b, 1.0F);
        
        if (!model.isBuiltInRenderer()) {
            renderModelCustom(model, stack, 1.0F, r, g, b);
        } else {
            stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
        }
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
    
    private static void renderModelCustom(IBakedModel model, ItemStack stack, float alpha, float r, float g, float b) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.ITEM);
        
        for (EnumFacing facing : EnumFacing.values()) {
            renderQuads(buffer, model.getQuads(null, facing, 42L), r, g, b, alpha);
        }
        renderQuads(buffer, model.getQuads(null, null, 42L), r, g, b, alpha);
        
        tessellator.draw();
    }
    
    private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, float r, float g, float b, float a) {
        for (BakedQuad bakedquad : quads) {
            renderer.addVertexData(bakedquad.getVertexData());
            
            if (bakedquad.hasTintIndex()) {
                for (int i = 1; i <= 4; ++i) {
                    renderer.putColorRGBA(renderer.getColorIndex(i), (int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
                }
            } else {
                for (int i = 1; i <= 4; ++i) {
                    renderer.putColorRGBA(renderer.getColorIndex(i), 255, 255, 255, (int)(a * 255));
                }
            }
            
            renderer.putNormal(0.0F, 0.0F, 1.0F);
        }
    }
    
    public static void renderIconColored(float x, float y, TextureAtlasSprite sprite, float width, float height, float alpha, int color) {
        // Извлекаем цвета из HEX
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int a = (int) (alpha * 255.0F);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        
        buffer.pos(x, y + height, 0.0D).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, a).endVertex();
        buffer.pos(x + width, y + height, 0.0D).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, a).endVertex();
        buffer.pos(x + width, y, 0.0D).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, a).endVertex();
        buffer.pos(x, y, 0.0D).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, a).endVertex();
        
        tessellator.draw();
    }
    
    public static void drawInnerBlit(int x1, int x2, int y1, int y2, int z, float minU, float maxU, float minV, float maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        
        bufferbuilder.pos(x1, y2, z).tex(minU, maxV).endVertex();
        bufferbuilder.pos(x2, y2, z).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(x2, y1, z).tex(maxU, minV).endVertex();
        bufferbuilder.pos(x1, y1, z).tex(minU, minV).endVertex();
        
        tessellator.draw();
    }
}
