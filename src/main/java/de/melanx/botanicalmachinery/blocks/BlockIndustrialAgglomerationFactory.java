package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockIndustrialAgglomerationFactory extends BlockBase {
    
    public BlockIndustrialAgglomerationFactory() {
        super(false);
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileIndustrialAgglomerationFactory();
    }
    
    @Override
    public int getGuiId() {
        return GuiHandler.INDUSTRIAL_AGGLOMERATION_FACTORY_ID;
    }
    
    @Override
    public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileIndustrialAgglomerationFactory tile = (TileIndustrialAgglomerationFactory) worldIn.getTileEntity(pos);
        return tile != null && tile.getProgress() > 0 ? 15 : 0;
    }
}
