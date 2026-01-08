package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalRunicAltar;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMechanicalRunicAltar extends BlockBase {
    
    public BlockMechanicalRunicAltar() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMechanicalRunicAltar();
    }
    
    @Override
    public int getGuiId() {
        return GuiHandler.MECHANICAL_RUNIC_ALTAR_ID;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileMechanicalRunicAltar tile = (TileMechanicalRunicAltar) worldIn.getTileEntity(pos);
        return tile != null && tile.getProgress() > 0 ? 15 : 0;
    }
}
