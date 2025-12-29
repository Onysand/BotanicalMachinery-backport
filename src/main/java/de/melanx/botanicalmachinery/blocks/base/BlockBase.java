package de.melanx.botanicalmachinery.blocks.base;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandHUD;

@SuppressWarnings({"NullableProblems", "deprecation"})
public abstract class BlockBase extends Block implements ITileEntityProvider, IWandHUD {
   
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  
    public static final AxisAlignedBB FRAME_SHAPE = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    private final boolean fullCube;
    
    public abstract int getGuiId();

    public BlockBase(boolean fullCube) {
        super(Material.ROCK);
        this.fullCube = fullCube;
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
    
    @Override
    public void renderHUD(Minecraft minecraft, ScaledResolution scaledResolution, World world, BlockPos blockPos) {
        ((TileBase) world.getTileEntity(blockPos)).renderHUD(minecraft);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileBase) {
                playerIn.openGui(BotanicalMachinery.instance, getGuiId() , worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }
        return false;
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return fullCube;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return fullCube;
    }
    
    @Override
    public BlockRenderLayer getBlockLayer() {
        return fullCube ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
