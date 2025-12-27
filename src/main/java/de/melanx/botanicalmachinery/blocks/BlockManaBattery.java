package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockManaBattery extends BlockBase {

    public final Variant variant;

    public BlockManaBattery(Variant variant) {
        super(true);
        this.variant = variant;
    }

    public enum Variant {
        CREATIVE,
        NORMAL
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileManaBattery();
    }
    
    @Override
    public int getGuiId() {
        return GuiHandler.MANA_BATTERY_ID;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileManaBattery tile = (TileManaBattery) worldIn.getTileEntity(pos);
        return tile != null ? tile.getCurrentMana() / tile.getManaCap() * 15 : 0;
    }
}
