package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.LibNames;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.recipe.RecipePureDaisy;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalDaisy extends TileMod implements ITickable {

    private int ticksToNextUpdate = 5;
    // Negative value = recipe completed
    private int[] workingTicks = new int[8];
    private final InventoryHandler inventory = new InventoryHandler();

    private final IItemHandlerModifiable lazyInventory = ItemStackHandlerWrapper.create(this.inventory);

    // The canExtract function makes it so that hoppers can only extract items when the recipe is done.
    private final IItemHandlerModifiable hopperInventory = ItemStackHandlerWrapper.create(this.inventory, slot -> this.workingTicks[slot] < 0, null);
    private final IFluidHandler fluidInventory = this.inventory;


    public TileMechanicalDaisy() {
        super();
    }
    
    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(LibNames.MECHANICAL_DAISY);
    }
    
    @Override
    public void update() {
        boolean hasSpawnedParticles = false;
        for (int i = 0; i < 8; i++) {
            RecipePureDaisy recipe = this.getRecipe(i);
            if (recipe != null) {
                //noinspection ConstantConditions
                if (!this.world.isRemote) {
                    if (this.workingTicks[i] >= recipe.getTime() * ServerConfig.multiplierDaisy) {
                        IBlockState state = recipe.getOutputState();
                        if (state.getBlock() != Blocks.AIR) {
                            //noinspection deprecation
                            this.inventory.setStackInSlot(i, state.getBlock().getItem(this.world, this.pos, state));
                        } else if (FluidRegistry.lookupFluidForBlock(state.getBlock()) != null) {
                            this.inventory.setStackInSlot(i, new FluidStack(FluidRegistry.lookupFluidForBlock(state.getBlock()), 1000));
                        }
                        this.workingTicks[i] = -1;
                    } else {
                        this.workingTicks[i] += 1;
                    }
                } else if (!hasSpawnedParticles && ClientConfig.everything && ClientConfig.daisy) {
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
        //noinspection ConstantConditions
        if (!this.world.isRemote) {
            if (this.ticksToNextUpdate <= 0) {
                this.ticksToNextUpdate = 5;
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            } else {
                this.ticksToNextUpdate -= 1;
            }
        }
    }

    @Nullable
    private RecipePureDaisy getRecipe(int slot) {
        IBlockState state = getState(slot);
        return this.getRecipe(state);
    }

    @Nullable
    public IBlockState getState(int slot) {
        IBlockState state = null;

        ItemStack stack = this.inventory.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemBlock) {
                state = ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
            }
        } else {
            FluidStack fluid = this.inventory.fluids.get(slot);
            if (fluid != null && fluid.amount >= 1000) {
                state = fluid.getFluid().getBlock().getDefaultState();
            }
        }
        return state;
    }

    @Nullable
    public IBlockState getState(ItemStack stack) {
        IBlockState state = null;

        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemBlock) {
                state = ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
            }
        }
        return state;
    }

    @Nullable
    public RecipePureDaisy getRecipe(IBlockState state) {
        if (this.world == null) return null;
        if (state == null) return null;

        for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
            if (recipe.matches(this.world, this.pos, null, state)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public void writePacketNBT(NBTTagCompound tag) {
        tag.setTag(TileTags.INVENTORY, this.inventory.serializeNBT());
        tag.setIntArray(TileTags.WORKING_TICKS, this.workingTicks);
    }

    @Override
    public void readPacketNBT(NBTTagCompound tag) {
        if (tag.hasKey(TileTags.INVENTORY)) {
            this.inventory.deserializeNBT(tag.getCompoundTag(TileTags.INVENTORY));
        }
        if (tag.hasKey(TileTags.WORKING_TICKS)) {
            this.workingTicks = tag.getIntArray(TileTags.WORKING_TICKS);
        }
    }

    @Nonnull
    @Override
    public <X> X getCapability(@Nonnull Capability<X> cap, @Nullable EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {

            // If the side is null (e.g we're in the gui) we return the normal inventory.
            // For world interactions (direction != null) we return the inventory that block slots of not finished recipes.
            //noinspection unchecked
            return (X) (side == null ? this.lazyInventory : this.hopperInventory);
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (X) this.fluidInventory;
        }
        return super.getCapability(cap, side);
    }

    public InventoryHandler getInventory() {
        return this.inventory;
    }
    
    public class InventoryHandler extends ItemStackHandler implements IFluidHandler {
        
        private final List<FluidStack> fluids = new ArrayList<>(8);
        private final List<IFluidTankProperties> tankProperties = new ArrayList<>(8);
        
        public InventoryHandler() {
            super(8);
            for (int i = 0; i < 8; i++) {
                this.fluids.add(null);
                tankProperties.add(new FluidTankProperties(fluids.get(i), 1000));
            }
            //fluids = NonNullList.from(FluidStack.EMPTY, new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.WATER, 1000));
        }
        
        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            if (!stack.isEmpty())
                this.fluids.set(slot, null);
            super.setStackInSlot(slot, stack);
        }
        
        public void setStackInSlot(int slot, FluidStack stack) {
            this.fluids.set(slot, stack);
            if (stack != null)
                super.setStackInSlot(slot, ItemStack.EMPTY);
            else
                this.onContentsChanged(slot); // setStackInSlot calls this as well
        }
        
        @Override
        public int getSlots() {
            return 8;
        }
        
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (this.fluids.get(slot) != null) {
                // The Slot is occupied by a fluid.
                return stack;
            } else {
                return super.insertItem(slot, stack, simulate);
            }
        }
        
        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (this.fluids.get(slot) != null) {
                // The Slot is occupied by a fluid.
                return ItemStack.EMPTY;
            } else {
                return super.extractItem(slot, amount, simulate);
            }
        }
        
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
        
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof ItemBlock && TileMechanicalDaisy.this.getRecipe(((ItemBlock) stack.getItem()).getBlock().getDefaultState()) != null;
        }
        
        public FluidStack getFluidInTank(int tank) {
            return this.fluids.get(tank);
        }
        
        public int getTankCapacity() {
            return 1000;
        }
        
        @Override
        public IFluidTankProperties[] getTankProperties() {
            return tankProperties.toArray(new IFluidTankProperties[0]);
        }
        
        public boolean isFluidValid(int tank, FluidStack stack) {
            return
                stack != null && TileMechanicalDaisy.this.getRecipe(stack.getFluid().getBlock().getDefaultState()) != null
                && this.fluids.get(tank) != null && this.fluids.get(tank).getFluid() == stack.getFluid();
        }
        
        @Override
        public int fill(FluidStack resource, boolean doFill) {
            int leftToFill = resource.amount;
            
            // Try to deposit the fluid to slots already filled with it
            for (int i = 0; i < 8; i++) {
                if (leftToFill <= 0)
                    break;
                if (!this.getStackInSlot(i).isEmpty())
                    continue;
                if (this.fluids.get(i).getFluid() == resource.getFluid()) {
                    int transfer = Math.min(leftToFill, this.getTankCapacity() - this.fluids.get(i).amount);
                    leftToFill -= transfer;
                    if (doFill) {
                        this.fluids.get(i).amount += transfer;
                        this.onContentsChanged(i);
                    }
                }
            }
            
            // If that did not work we use an empty slot
            for (int i = 0; i < 8; i++) {
                if (leftToFill <= 0)
                    break;
                if (!this.getStackInSlot(i).isEmpty())
                    continue;
                if (this.fluids.get(i) == null) {
                    int transfer = Math.min(leftToFill, this.getTankCapacity());
                    leftToFill -= transfer;
                    if (doFill) {
                        this.fluids.set(i, new FluidStack(resource.getFluid(), transfer));
                        this.onContentsChanged(i);
                    }
                }
            }
            return resource.amount - leftToFill;
        }
        
        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            int leftToDrain = resource.amount;
            
            for (int i = 0; i < 8; i++) {
                if (leftToDrain <= 0)
                    break;
                if (!this.getStackInSlot(i).isEmpty())
                    continue;
                if (this.fluids.get(i).getFluid() == resource.getFluid()) {
                    int transfer = Math.min(this.fluids.get(i).amount, leftToDrain);
                    leftToDrain -= transfer;
                    if (doDrain) {
                        this.fluids.get(i).amount -= transfer;
                        if (this.fluids.get(i).amount <= 0)
                            this.fluids.set(i, null);
                        this.onContentsChanged(i);
                    }
                }
            }
            
            if (resource.amount - leftToDrain > 0) {
                return new FluidStack(resource.getFluid(), resource.amount - leftToDrain);
            } else {
                return null;
            }
        }
        
        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            int leftToDrain = maxDrain;
            Fluid drainFluid = null;
            
            for (int i = 0; i < 8; i++) {
                if (leftToDrain <= 0)
                    break;
                if (!this.getStackInSlot(i).isEmpty())
                    continue;
                if (drainFluid == null || drainFluid == this.fluids.get(i).getFluid()) {
                    int transfer = Math.min(this.fluids.get(i).amount, leftToDrain);
                    leftToDrain -= transfer;
                    if (transfer > 0)
                        drainFluid = this.fluids.get(i).getFluid();
                    if (doDrain) {
                        this.fluids.get(i).amount -= transfer;
                        if (this.fluids.get(i).amount <= 0)
                            this.fluids.set(i, null);
                        this.onContentsChanged(i);
                    }
                }
            }
            
            if (drainFluid == null) {
                return null;
            } else {
                return new FluidStack(drainFluid, maxDrain - leftToDrain);
            }
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
        protected void onContentsChanged(int slot) {
            for (int i = 0; i < 8; i++) {
                fluids.set(i, tankProperties.get(i).getContents());
            }
            
            TileMechanicalDaisy.this.markDirty();
        }
    }
}
