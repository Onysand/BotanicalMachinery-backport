package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalDaisy;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockMechanicalDaisy extends Block {
    
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0, 0, 0, 1, 0.7125, 1);
    
    private static final AxisAlignedBB COLLISION_BASE = new AxisAlignedBB(0, 0, 0, 1, 0.125, 1);
    private static final AxisAlignedBB COLLISION_TOP = new AxisAlignedBB(0.3125, 0.125, 0.3125, 0.6875, 0.1875, 0.6875);

    public BlockMechanicalDaisy() {
        super(Material.ROCK);
        setHardness(2);
        setResistance(10);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMechanicalDaisy();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileMechanicalDaisy) {
                player.openGui(BotanicalMachinery.instance, GuiHandler.MECHANICAL_DAISY_ID, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess access, BlockPos pos) {
        return 0;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BASE);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_TOP);
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
}
