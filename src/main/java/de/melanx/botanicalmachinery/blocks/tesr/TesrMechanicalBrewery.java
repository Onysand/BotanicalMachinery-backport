package de.melanx.botanicalmachinery.blocks.tesr;

import com.google.common.collect.ImmutableMap;
import de.melanx.botanicalmachinery.blocks.base.HorizontalRotatedTesr;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.item.Item16Colors;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.Map;

public class TesrMechanicalBrewery extends HorizontalRotatedTesr<TileMechanicalBrewery> {

    public static final Map<Item, Integer> INGREDIENT_COLORS = ImmutableMap.<Item, Integer>builder()
            .put(Items.NETHER_WART, 0xbe3f4a)
            .put(Items.PRISMARINE_CRYSTALS, 0x91c5b7)
            .put(Items.GLOWSTONE_DUST, 0xffbc5e)
            .put(Items.GOLDEN_APPLE, 0xdba213)
            .put(Items.POTATO, 0xe9ba62)
            .put(Items.SUGAR, 0xd5d5df)
            .put(Items.GOLD_NUGGET, 0xf9f969)
            .put(Items.IRON_INGOT, 0xd8d8d8)
            .put(Items.LEATHER, 0xc65c35)
            .put(Items.MAGMA_CREAM, 0xd58520)
            .put(Items.FERMENTED_SPIDER_EYE, 0x65062b)
            .put(Items.DYE, 0x345ec3)
            .put(Items.FIRE_CHARGE, 0xeeac18)
            .put(Items.SPECKLED_MELON, 0xc94908)
            .put(Items.GHAST_TEAR, 0x9fc3c3)
            .put(Items.GUNPOWDER, 0x727272)
            .put(Items.ROTTEN_FLESH, 0x834418)
            .put(Items.BONE, 0xfcfbed)
            .put(Items.STRING, 0xdbdbdb)
            .put(Items.ENDER_PEARL, 0x349988)
            .put(Items.BLAZE_POWDER, 0xffe000)
            .put(ModItems.manaResource, 0x006bff)
            .put(Items.SPIDER_EYE, 0x9d1e2d)
            .put(Items.GOLDEN_CARROT, 0xdba213)
            .put(Items.PAPER, 0xe9eaeb)
            .put(Items.APPLE, 0xdd1725)
            .put(Items.FEATHER, 0x969696)
            .put(Items.CARROT, 0xff8e09)
            .put(Items.REDSTONE, 0xea0400)
            .put(Items.FISH, 0xc6a271)
            .put(Items.QUARTZ, 0xddd4c6)
            .put(Items.SNOWBALL, 0xffffff)
            .put(Items.EMERALD, 0x17dd62)
            .put(Items.MELON, 0xbf3123)
            .build();

    private final int waterColor;

    public TesrMechanicalBrewery() {
        super();
        this.waterColor = FluidRegistry.WATER.getColor();
    }

    @Override
    protected void doRender(TileMechanicalBrewery tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!ClientConfig.everything || !ClientConfig.brewery)
            return;
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        int light = getWorld().getLight(tile.getPos());
        int slotToMove = -1;
        double travelCenter = 1;
        Float vialRotate = null;
        double vialDown = 0;
        boolean showOutput = false;

        if (tile.getProgress() > 0) {
            double progress = tile.getProgress() / (double) tile.getMaxProgress();

            int segments = 3;
            for (int i = 1; i <= 6; i++) {
                if (!tile.getInventory().getStackInSlot(i).isEmpty())
                    segments += 1;
            }

            double segment = 1d / segments;
            double segmentProgress = (progress % segment) * segments;

            if (progress >= 1 - segment) {
                slotToMove = Integer.MAX_VALUE;

                double progressMinusHalf = segmentProgress - 0.5;
                vialDown = (progressMinusHalf * progressMinusHalf) - 0.25;
                vialRotate = (float) (480 * vialDown);
                vialDown = 1.8 * vialDown;
                showOutput = progressMinusHalf >= 0;
                this.renderFluid(buffer, partialTicks, light, (float) (1 - segmentProgress), this.getTargetColor(tile));
            } else if (progress >= 1 - (2 * segment)) {
                slotToMove = Integer.MAX_VALUE;

                int fromColor = this.waterColor;
                for (int i = 6; i >= 1; i--) {
                    if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                        fromColor = this.getColor(tile.getInventory().getStackInSlot(i));
                        break;
                    }
                }
                this.renderFluid(buffer, partialTicks, light, 1, fromColor, this.getTargetColor(tile), segmentProgress);
            } else if (progress < segment) {
                this.renderFluid(buffer, partialTicks, light, (float) segmentProgress, this.waterColor);
            } else {
                int idx = (int) ((progress - segment) / segment);

                int fromColor = this.waterColor;
                int toColor = this.waterColor;

                for (int i = 1; i <= 6; i++) {
                    if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                        if (idx <= 0) {
                            slotToMove = i;
                            travelCenter = segmentProgress;
                            toColor = this.getColor(tile.getInventory().getStackInSlot(i));
                            break;
                        } else {
                            if (idx == 1) {
                                fromColor = this.getColor(tile.getInventory().getStackInSlot(i));
                            }
                            idx -= 1;
                        }
                    }
                }

                this.renderFluid(buffer, partialTicks, light, 1, fromColor, toColor, segmentProgress);
            }
        }

        ItemStack topStack = tile.getInventory().getStackInSlot(7);
        if (showOutput && !tile.getCurrentOutput().isEmpty()) {
            topStack = tile.getCurrentOutput();
        } else if (topStack.isEmpty() || tile.getProgress() > 0) {
            topStack = tile.getInventory().getStackInSlot(0);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.8 + vialDown, 0.5);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.rotate((ClientTickHandler.ticksInGame + partialTicks) / 1.3f, 0, 1, 0);
        if (vialRotate != null)
            GlStateManager.rotate(vialRotate, 1, 0 ,0);

        Minecraft.getMinecraft().getRenderItem().renderItem(topStack, ItemCameraTransforms.TransformType.GROUND);

        GlStateManager.popMatrix();

        int itemAmount = 0;
        for (int i = 1; i <= 6; i++) {
            if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                itemAmount += 1;
            }
        }

        double angle = 360d / itemAmount;
        float time = ClientTickHandler.ticksInGame + partialTicks;

        int idx = 0;
        for (int i = 1; i <= 6; i++) {
            if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                int idxNow = idx++;
                if (i < slotToMove)
                    continue;
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5, 0.7, 0.5);
                GlStateManager.scale(0.3f, 0.3f, 0.3f);
                GlStateManager.rotate((float) -((angle * idxNow) + time), 0, 1, 0);
                if (i == slotToMove) {
                    GlStateManager.translate((1 - travelCenter) * 1.125, travelCenter * -1, (1 - travelCenter) * 0.25);
                    GlStateManager.rotate((float) (90 * travelCenter), 1, 0 ,0);
                } else {
                    GlStateManager.translate(1.125, 0, 0.25);
                }
                GlStateManager.rotate(90f, 0, 1, 0);
                GlStateManager.translate(0, 0.075 * Math.sin((time + (idxNow * 10)) / 5d), 0);

                Minecraft.getMinecraft().getRenderItem().renderItem(tile.getInventory().getStackInSlot(i), ItemCameraTransforms.TransformType.GROUND);

                GlStateManager.popMatrix();
            }
        }
    }

    private int getColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return this.waterColor;
        } else if (INGREDIENT_COLORS.containsKey(stack.getItem())) {
            return INGREDIENT_COLORS.get(stack.getItem());
        } else if (stack.getItem() instanceof ItemDye) {
            return EnumDyeColor.byDyeDamage(stack.getItemDamage()).getColorValue();
        } else if (stack.getItem() instanceof ItemBlock) {
            //noinspection deprecation
            return ((ItemBlock) stack.getItem()).getBlock().getMaterial(((ItemBlock) stack.getItem()).getBlock().getDefaultState()).getMaterialMapColor().colorValue;
        } else if (stack.getItem() instanceof Item16Colors) {
            return EnumDyeColor.byMetadata(stack.getMetadata()).getColorValue();
        } else {
            return this.waterColor;
        }
    }

    private int getTargetColor(TileMechanicalBrewery tile) {
        if (tile.getCurrentOutput().getItem() instanceof IBrewItem) {
            return ((IBrewItem) tile.getCurrentOutput().getItem()).getBrew(tile.getCurrentOutput()).getColor(tile.getCurrentOutput());
        } else {
            return this.waterColor;
        }
    }

    private void renderFluid(@Nonnull BufferBuilder buffer, float partialTicks, int light, float fillLevel, int colorFrom, int colorTo, double progress) {
        int fromRed = colorFrom >> 16 & 255;
        int fromGreen = colorFrom >> 8 & 255;
        int fromBlue = colorFrom & 255;

        int toRed = colorTo >> 16 & 255;
        int toGreen = colorTo >> 8 & 255;
        int toBlue = colorTo & 255;

        int red = (int) Math.round(fromRed + ((toRed - fromRed) * progress));
        int green = (int) Math.round(fromGreen + ((toGreen - fromGreen) * progress));
        int blue = (int) Math.round(fromBlue + ((toBlue - fromBlue) * progress));

        int color = (red << 16) | (green << 8) | blue;

        this.renderFluid(buffer, partialTicks, light, fillLevel, color);
    }

    private void renderFluid(BufferBuilder buffer, float partialTicks, int light, float fillLevel, int color) {
        GlStateManager.pushMatrix();
        
        GlStateManager.scale(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
        GlStateManager.translate(4.0f, 3.0f + (4.4f * fillLevel), 4.0f);
        GlStateManager.rotate(90, 1, 0, 0);
        
        ResourceLocation stillLocation = FluidRegistry.WATER.getStill();
        TextureAtlasSprite sprite = Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getAtlasSprite(stillLocation.toString());
        
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        
        GlStateManager.color(r, g, b, a);
        
        int j = light >> 16 & 65535;
        int k = light & 65535;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        
        double size = 8.0;
        buffer.pos(0, size, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(size, size, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(size, 0, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        
        Tessellator.getInstance().draw();
        
        GlStateManager.popMatrix();
    }
}
