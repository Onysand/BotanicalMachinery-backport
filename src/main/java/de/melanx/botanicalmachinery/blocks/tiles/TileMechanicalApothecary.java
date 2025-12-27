package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.TileTags;
import de.melanx.botanicalmachinery.helper.RecipeHelper;
import de.melanx.botanicalmachinery.util.inventory.BaseItemStackHandler;
import de.melanx.botanicalmachinery.util.inventory.ItemStackHandlerWrapper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.core.handler.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TileMechanicalApothecary extends TileMod implements ITickable {

    public static final int WORKING_DURATION = 20;
    public static final int FLUID_CAPACITY = 8000;

    private final IItemHandlerModifiable handler = ItemStackHandlerWrapper.createFromSup(this::getInventory);
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(21, slot -> {
        this.update = true;
        this.sendPacket = true;
    }, this::isValidStack);
    private final ModdedFluidTank fluidInventory = new ModdedFluidTank(new FluidStack(FluidRegistry.WATER, 0), FLUID_CAPACITY);
    private final IFluidHandler fluidHandler = this.fluidInventory;
    private RecipePetals recipe = null;
    private boolean initDone;
    private int progress;
    private boolean update;
    private boolean sendPacket;
    private ItemStack currentOutput = ItemStack.EMPTY;

    public TileMechanicalApothecary() {
        super();
        this.inventory.setInputSlots(IntStream.range(1, 17).toArray());
        this.inventory.setOutputSlots(IntStream.range(17, 21).toArray());
    }

    @Nonnull
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Nonnull
    public FluidTank getFluidInventory() {
        return this.fluidInventory;
    }

    public boolean isValidStack(int slot, ItemStack stack) {
        if (slot == 0) return OreDictionary.getOres("listAllseed").contains(stack);
        else if (Arrays.stream(this.inventory.getInputSlots()).anyMatch(x -> x == slot)) {
            for (RecipePetals r : BotaniaAPI.petalRecipes) {
                return r.getInputs().stream().anyMatch(input -> RecipeHelper.isInputMatch(input, stack));
            }
        }
        return true;
    }

    private void updateRecipe() {
        if (this.world != null && !this.world.isRemote) {
            List<ItemStack> stacks = new ArrayList<>(this.inventory.getStacks());
            RecipeHelper.removeFromList(stacks, IntStream.range(17, stacks.size() - 1).toArray(), new int[]{0});
            Map<Item, Integer> items = RecipeHelper.getInvItems(stacks);

            for (RecipePetals recipe : BotaniaAPI.petalRecipes) {
                if (recipe.matches(this.inventory) && !this.inventory.getStackInSlot(0).isEmpty() && this.fluidInventory.getFluidAmount() >= 1000) {
                    this.recipe = recipe;
                    this.currentOutput = this.recipe.getOutput().copy();
                    this.sendPacket = true;
                    return;
                }}
        }
        this.currentOutput = ItemStack.EMPTY;
        this.recipe = null;
    }

    @Override
    public void update() {
        if (this.sendPacket) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            this.sendPacket = false;
        }
        if (this.world != null && !this.world.isRemote) {
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }
            boolean done = false;
            if (this.recipe != null) {
                if (this.progress <= getRecipeDuration()) {
                    ++this.progress;
                    if (this.progress >= getRecipeDuration()) {
                        ItemStack output = this.recipe.getOutput().copy();
                        for (Object input : this.recipe.getInputs()) {
                            for (ItemStack stack : this.inventory.getStacks()) {
                                if (RecipeHelper.isInputMatch(input, stack)) {
                                    stack.shrink(1);
                                    break;
                                }
                            }
                        }
                        FluidStack fluid = this.fluidInventory.getFluid();
                        fluid.amount -= 1000;
                        this.fluidInventory.setFluid(fluid);
                        this.inventory.getStackInSlot(0).shrink(1);
                        this.putIntoOutput(output);
                        this.update = true;
                        done = true;
                    }
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            if ((done && this.progress > 0) || (this.recipe == null && this.progress > 0)) {
                this.progress = 0;
                this.markDirty();
                this.markDispatchable();
            }
            if (this.update) {
                this.updateRecipe();
                this.update = false;
            }
        } else if (this.world != null && ClientConfig.everything && ClientConfig.apothecary) {
            if (this.fluidInventory.getFluidAmount() > 0) {
                if (this.progress > getRecipeDuration() - 5) {
                    for (int i = 0; i < 5; i++) {
                        Botania.proxy.sparkleFX(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), 10);
                    }
                    this.world.playSound(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, ModSounds.altarCraft, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                } else {
                    for (int slot = 0; slot < this.inventory.getSlots(); slot++) {
                        ItemStack stack = this.inventory.getStackInSlot(slot);
                        if (stack.isEmpty()) {
                            continue;
                        }

                        if (this.world.rand.nextFloat() >= 0.97f) {
                            int color = 0x888888;
                            float red = (float) (color >> 16 & 255) / 255f;
                            float green = (float) (color >> 8 & 255) / 255f;
                            float blue = (float) (color & 255) / 255f;
                            if (Math.random() >= 0.75) {
                                this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1F, 10.0F);
                            }
                            Botania.proxy.sparkleFX(this.pos.getX() + 0.3 + (this.world.rand.nextDouble() * 0.4), this.pos.getY() + 0.6, this.pos.getZ() + 0.3 + (this.world.rand.nextDouble() * 0.4), red, green, blue, this.world.rand.nextFloat(), 10);
                        }
                    }
                }
            }
        }
    }

    private void putIntoOutput(ItemStack stack) {
        for (int i : this.inventory.getOutputSlots()) {
            if (stack.isEmpty()) break;
            ItemStack slotStack = this.inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                this.inventory.getUnrestricted().insertItem(i, stack.copy(), false);
                break;
            } else if ((slotStack.getItem() == stack.getItem() && slotStack.getCount() < slotStack.getMaxStackSize())) {
                ItemStack left = this.inventory.getUnrestricted().insertItem(i, stack, false);
                if (left != ItemStack.EMPTY) stack = left;
                else break;
            }
        }
    }

    private void markDispatchable() {
        this.sendPacket = true;
    }

    public int getProgress() {
        return this.progress;
    }

    public static int getRecipeDuration() {
        return WORKING_DURATION * ServerConfig.multiplierApothecary;
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        cmp.setTag(TileTags.INVENTORY, this.getInventory().serializeNBT());
        final NBTTagCompound tankTag = new NBTTagCompound();
        this.getFluidInventory().getFluid().writeToNBT(tankTag);
        cmp.setTag(TileTags.FLUID, tankTag);
        cmp.setInteger(TileTags.PROGRESS, this.progress);
        cmp.setTag(TileTags.CURRENT_OUTPUT, this.currentOutput.serializeNBT());
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        this.getInventory().deserializeNBT(cmp.getCompoundTag(TileTags.INVENTORY));
        this.fluidInventory.setFluid(FluidStack.loadFluidStackFromNBT(cmp.getCompoundTag(TileTags.FLUID)));
        this.progress = cmp.getInteger(TileTags.PROGRESS);
        this.currentOutput = new ItemStack(cmp.getCompoundTag(TileTags.CURRENT_OUTPUT));
    }
    
    @Nonnull
    @Override
    public <X> X getCapability(@Nonnull Capability<X> cap, @Nullable EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (X) this.handler;
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (X) this.fluidHandler;
        }
        return super.getCapability(cap, side);
    }

    public ItemStack getCurrentOutput() {
        return this.currentOutput;
    }
    
    private class ModdedFluidTank extends FluidTank {
        public ModdedFluidTank(FluidStack fluidStack, int capacity) {
            super(fluidStack, capacity);
        }

        @Override
        protected void onContentsChanged() {
            TileMechanicalApothecary.this.sendPacket = true;
            TileMechanicalApothecary.this.update = true;
        }
        
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid.getFluid() == FluidRegistry.WATER;
        }
        
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }
        
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
    }
}
