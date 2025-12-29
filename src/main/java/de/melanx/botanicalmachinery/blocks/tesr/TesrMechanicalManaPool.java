package de.melanx.botanicalmachinery.blocks.tesr;

import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.config.BMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.IconHelper;

public class TesrMechanicalManaPool extends HorizontalRotatedTesr<TileMechanicalManaPool> {

    public static final double INNER_POOL_HEIGHT = 4.5 / 16;
    public static final double POOL_BOTTOM_HEIGHT = 1.15 / 16;

    public TesrMechanicalManaPool() {
        super();
    }

    @Override
    protected void doRender(TileMechanicalManaPool tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!BMConfig.CLIENT.rendering.all || !BMConfig.CLIENT.rendering.manaPool)
            return;

        ItemStack catalystStack = tile.getInventory().getStackInSlot(0);
        if (!catalystStack.isEmpty() && catalystStack.getItem() instanceof ItemBlock && ((ItemBlock) catalystStack.getItem()).getBlock() instanceof IPoolOverlayProvider) {
            IPoolOverlayProvider catalyst = (IPoolOverlayProvider) ((ItemBlock) catalystStack.getItem()).getBlock();
            TextureAtlasSprite sprite = catalyst.getIcon(tile.getWorld(), tile.getPos());

            GlStateManager.pushMatrix();

            GlStateManager.translate(2 / 16d, POOL_BOTTOM_HEIGHT, 2 / 16d);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
            
            float renderAlpha = (float) ((Math.sin((double) (ClientTickHandler.ticksInGame + partialTicks) / 20.0D) + 1.0D) * 0.3D + 0.2D);
            
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, renderAlpha);
            
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            
            IconHelper.renderIconIn3D(
                Tessellator.getInstance(),
                sprite.getMinU(), sprite.getMinV(),
                sprite.getMaxU(), sprite.getMaxV(),
                12, 12,
                0.001F
            );
            
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if (tile.getCurrentMana() > 0) {
            double amount = tile.getCurrentMana() / (double) tile.getManaCap();

            GlStateManager.pushMatrix();
            GlStateManager.translate(3 / 16d, POOL_BOTTOM_HEIGHT + (amount * INNER_POOL_HEIGHT), 3 / 16d);
            GlStateManager.rotate(90f, 1, 0 ,0);
            GlStateManager.scale(1 / 16f, 1 / 16f, 1 / 16f);
            
            GlStateManager.enableBlend();
            TextureAtlasSprite sprite = MiscellaneousIcons.INSTANCE.manaWater;
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            
            IconHelper.renderIconIn3D(
                Tessellator.getInstance(),
                sprite.getMinU(), sprite.getMinV(),
                sprite.getMaxU(), sprite.getMaxV(),
                sprite.getIconWidth(),
                sprite.getIconHeight(),
                0.0625F
            );
            
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        ItemStack input = tile.getInventory().getStackInSlot(1);
        ItemStack output = tile.getInventory().getStackInSlot(2);

        if (!input.isEmpty() || !output.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(5 / 16d, 7 / 16d, 8 / 16d);

            if (!output.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(7 / 16f, 7 / 16f, 7 / 16f);
                GlStateManager.rotate(ClientTickHandler.ticksInGame + partialTicks, 0, 1, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(output, ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }

            GlStateManager.translate(6 / 16d, 0, 0);

            if (!input.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(7 / 16f, 7 / 16f, 7 / 16f);
                GlStateManager.rotate(ClientTickHandler.ticksInGame % 360, 0, 1, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(input, ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }
    }
}
