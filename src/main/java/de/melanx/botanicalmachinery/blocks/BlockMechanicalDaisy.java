package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMechanicalDaisy extends BlockBase {
    
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0, 0, 0, 1, 0.7125, 1);

    public BlockMechanicalDaisy() {
        super(false);
        setHardness(2);
        setResistance(10);
    }
	
	@Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMechanicalDaisy();
    }
    
    @Override
    public int getGuiId() {
        return GuiHandler.MECHANICAL_DAISY_ID;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess access, BlockPos pos) {
        return 0;
    }
    
    @SuppressWarnings("NullableProblems")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileMechanicalDaisy tile = (TileMechanicalDaisy) worldIn.getTileEntity(pos);
        int x = 0;
        if (tile != null) {
            for (int i = 0; i < 8; i++) {
                if (!tile.getInventory().getStackInSlot(i).isEmpty()) {
                    x++;
                }
            }
            return x;
        }
        return 0;
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMechanicalDaisy();
    }
}
