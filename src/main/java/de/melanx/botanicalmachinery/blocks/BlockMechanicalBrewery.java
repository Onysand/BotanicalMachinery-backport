package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMechanicalBrewery extends BlockBase {
    
    public BlockMechanicalBrewery() {
        super(false);
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMechanicalBrewery();
    }
    
    @Override
    public int getGuiId() {
        return GuiHandler.MECHANICAL_BREWERY_ID;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileMechanicalBrewery tile = (TileMechanicalBrewery) worldIn.getTileEntity(pos);
        return tile != null && tile.getProgress() > 0 ? 15 : 0;
    }
}
