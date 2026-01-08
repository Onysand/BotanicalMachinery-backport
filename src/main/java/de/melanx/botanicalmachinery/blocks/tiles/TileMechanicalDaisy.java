package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.config.BMConfig;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandlerModifiable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePureDaisy;
import vazkii.botania.common.Botania;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TileMechanicalDaisy extends TileBase {
    
    private int[] workingTicks = new int[8];
    private final TileMechanicalDaisy.InventoryHandler inventory = new InventoryHandler();
    
    public TileMechanicalDaisy() {
        super(0);
    }
    
    @Override
    protected String getName() {
        return LibNames.MECHANICAL_DAISY;
    }
    
    @Nonnull
    @Override
    public TileMechanicalDaisy.InventoryHandler getInventory() {
        return this.inventory;
    }
    
    @Override
    public <X> X customCapabilityHandle(@Nonnull Capability<X> cap, EnumFacing facing) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (X) this.inventory;
        }
        
        return null;
    }
    
    @Override
    protected IItemHandlerModifiable createItemHandler(Supplier<IItemHandlerModifiable> inventory) {
        return ItemStackHandlerWrapper.createFromSup(
            inventory,
            (slot) -> this.workingTicks[slot] <= 0,
	        this::isValidStack
        );
    }
    
    @Override
    public boolean isValidStack(int slot, ItemStack stack) {
        return this.inventory.isItemValid(slot, stack);
    }
    
    @Override
    public void update() {
        super.update();
        
        boolean hasSpawnedParticles = false;
        for (int i = 0; i < 8; i++) {
            RecipePureDaisy recipe = this.getRecipe(i);
            if (recipe != null) {
                if (!this.world.isRemote) {
                    if (this.workingTicks[i] >= recipe.getTime() * BMConfig.SERVER.multipliers.daisy) {
                        IBlockState state = recipe.getOutputState();
                        if (state.getBlock() != Blocks.AIR) {
                            this.inventory.setStackInSlot(i, state.getBlock().getItem(this.world, this.pos, state));
                        } else if (FluidRegistry.lookupFluidForBlock(state.getBlock()) != null) {
                            this.inventory.setStackInSlot(i, new FluidStack(FluidRegistry.lookupFluidForBlock(state.getBlock()), 1000));
                        }
                        this.workingTicks[i] = -1;
                        this.markDispatchable();
                    } else {
                        this.workingTicks[i] += 1;
                    }
                } else if (!hasSpawnedParticles && BMConfig.CLIENT.rendering.all && BMConfig.CLIENT.rendering.daisy) {
                    hasSpawnedParticles = true;
                    double x = this.pos.getX() + Math.random();
                    double y = this.pos.getY() + Math.random() + 0.25D;
                    double z = this.pos.getZ() + Math.random();
                    Botania.proxy.wispFX(x, y, z, 1.0F, 1.0F, 1.0F, (float) Math.random() / 2.0F, 0, 0, 0);
                }
            } else {
                if (this.workingTicks[i] < 0 && !this.inventory.getStackInSlot(i).isEmpty()) {
                    this.workingTicks[i] = -1;
                } else {
                    this.workingTicks[i] = 0;
                }
            }
        }
    }
    
    @Nullable
    private RecipePureDaisy getRecipe(int slot) {
        IBlockState state = getState(slot);
        if (state == null) return null;
        
        for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
            if (recipe.matches(this.world, this.pos, null, state)) {
                return recipe;
            }
        }
        return null;
    }
    
    @Nullable
    public IBlockState getState(int slot) {
        ItemStack stack = this.inventory.getStackInSlot(slot);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
            return ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
        }
        FluidStack fluid = this.inventory.getFluidInTank(slot);
        if (fluid != null && fluid.amount >= 1000) {
            Block b = fluid.getFluid().getBlock();
            if (b != null) return b.getDefaultState();
        }
        return null;
    }
    
    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);
        cmp.setIntArray(TileTags.WORKING_TICKS, this.workingTicks);
    }
    
    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);
        if (cmp.hasKey(TileTags.WORKING_TICKS)) {
            this.workingTicks = cmp.getIntArray(TileTags.WORKING_TICKS);
        }
    }
    
    public boolean isFluidValid(FluidStack fluidStack) {
        if (fluidStack == null || fluidStack.getFluid() == null) return false;
        
        Block fluidBlock = fluidStack.getFluid().getBlock();
        if (fluidBlock == null) return false;
        
        IBlockState state = fluidBlock.getDefaultState();
        
        for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
            if (recipe.matches(this.world, this.pos, null, state)) {
                return true;
            }
        }
        return false;
    }
    
    public class InventoryHandler extends BaseItemStackHandler implements IFluidHandler {
        
        private final List<FluidStack> fluids = new ArrayList<>(8);
        private final List<IFluidTankProperties> tankProperties = new ArrayList<>(8);
        
        public InventoryHandler() {
            super(8);
            for (int i = 0; i < 8; i++) {
                this.fluids.add(null);
                tankProperties.add(new FluidTankProperties(null, getTankCapacity()));
            }
        }
        
        public int getTankCapacity() {
            return 1000;
        }
        
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            if (!stack.isEmpty()) this.fluids.set(slot, null);
            super.setStackInSlot(slot, stack);
        }
        
        public void setStackInSlot(int slot, FluidStack stack) {
            this.fluids.set(slot, stack);
            if (stack != null) super.setStackInSlot(slot, ItemStack.EMPTY);
            else this.onContentsChanged(slot);
        }
        
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) return false;
            if (fluids.get(slot) != null) return false;
            Block b = ((ItemBlock) stack.getItem()).getBlock();
            for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
                if (recipe.matches(TileMechanicalDaisy.this.getWorld(), TileMechanicalDaisy.this.getPos(), null, b.getDefaultState())) return true;
            }
            return false;
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
        
        // --- IFluidHandler Implementation ---
        
        @Override
        public IFluidTankProperties[] getTankProperties() {
            for (int i = 0; i < 8; i++) {
                tankProperties.set(i, new FluidTankProperties(fluids.get(i), 1000));
            }
            return tankProperties.toArray(new IFluidTankProperties[0]);
        }
        
        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (!TileMechanicalDaisy.this.isFluidValid(resource)) return 0;
            
            for (int i = 0; i < 8; i++) {
                if (this.getStackInSlot(i).isEmpty() && (this.fluids.get(i) == null || this.fluids.get(i).isFluidEqual(resource))) {
                    int current = this.fluids.get(i) == null ? 0 : this.fluids.get(i).amount;
                    int fillable = 1000 - current;
                    int canFill = Math.min(resource.amount, fillable);
                    if (doFill && canFill > 0) {
                        if (this.fluids.get(i) == null) this.fluids.set(i, new FluidStack(resource.getFluid(), canFill));
                        else this.fluids.get(i).amount += canFill;
                        this.onContentsChanged(i);
                    }
                    return canFill;
                }
            }
            return 0;
        }
        
        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (resource == null) return null;
            for (int i = 0; i < 8; i++) {
                if (this.fluids.get(i) != null && this.fluids.get(i).isFluidEqual(resource)) {
                    return this.drain(i, resource.amount, doDrain);
                }
            }
            return null;
        }
        
        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            for (int i = 0; i < 8; i++) {
                if (this.fluids.get(i) != null) {
                    return this.drain(i, maxDrain, doDrain);
                }
            }
            return null;
        }
        
        private FluidStack drain(int slot, int amount, boolean doDrain) {
            FluidStack stack = this.fluids.get(slot);
            if (stack == null) return null;
            int drainable = Math.min(amount, stack.amount);
            FluidStack ret = new FluidStack(stack.getFluid(), drainable);
            if (doDrain) {
                stack.amount -= drainable;
                if (stack.amount <= 0) this.fluids.set(slot, null);
                this.onContentsChanged(slot);
            }
            return ret;
        }
        
        public FluidStack getFluidInTank(int tank) {
            return this.fluids.get(tank);
        }
        
        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = super.serializeNBT();
            NBTTagList tag = new NBTTagList();
            for (int i = 0; i < 8; i++) {
                FluidStack stack = this.fluids.get(i);
                NBTTagCompound fluidNbt = new NBTTagCompound();
                if (stack != null) stack.writeToNBT(fluidNbt);
                tag.appendTag(fluidNbt);
            }
            nbt.setTag("fluids", tag);
            return nbt;
        }
        
        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            super.deserializeNBT(nbt);
            if (nbt.hasKey("fluids")) {
                NBTTagList tag = nbt.getTagList("fluids", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < 8; i++) {
                    NBTTagCompound fluidNbt = tag.getCompoundTagAt(i);
                    this.fluids.set(i, FluidStack.loadFluidStackFromNBT(fluidNbt));
                }
            }
        }
        
        @Override
        public void onContentsChanged(int slot) {
            TileMechanicalDaisy.this.markDirty();
            TileMechanicalDaisy.this.markDispatchable();
        }
    }
}