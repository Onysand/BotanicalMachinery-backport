package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMechanicalApothecary extends BlockBase {
    
    public BlockMechanicalApothecary() {
        super(false);
        setHardness(2);
        setResistance(10);
    }
	
	@Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMechanicalApothecary();
    }
    
    @Override
    public int getGuiId() {
        return GuiHandler.MECHANICAL_APOTHECARY_ID;
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);

            ItemStack held = playerIn.getHeldItemMainhand();
            @SuppressWarnings("ConstantConditions")
            FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainer(held, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), 1000, playerIn, true);
            if (fluidActionResult.isSuccess()) {
                if (!playerIn.isCreative()) {
                    playerIn.addItemStackToInventory(fluidActionResult.getResult());
                    held.shrink(1);
                }
                return true;
            }

            if (tile instanceof TileMechanicalApothecary) {
                playerIn.openGui(BotanicalMachinery.instance, GuiHandler.MECHANICAL_APOTHECARY_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
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
    public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileMechanicalApothecary tile = (TileMechanicalApothecary) worldIn.getTileEntity(pos);
        return tile != null && tile.getProgress() > 0 ? 15 : 0;
    }
}
