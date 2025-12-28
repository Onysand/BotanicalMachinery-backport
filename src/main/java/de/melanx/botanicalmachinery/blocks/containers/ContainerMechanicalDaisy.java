package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerMechanicalDaisy extends ContainerBase<TileMechanicalDaisy> {

    private final TileMechanicalDaisy.InventoryHandler inventory;

    public ContainerMechanicalDaisy(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player) {
        super(world, pos, playerInventory, player, 8, 8);

        this.inventory = this.tile.getInventory();
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 0, 79, 16));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 1, 100, 16));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 2, 121, 16));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 3, 79, 37));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 4, 121, 37));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 5, 79, 58));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 6, 100, 58));
        this.addSlotToContainer(new ItemAndFluidSlot(this.inventory, 7, 121, 58));

        this.layoutPlayerInventorySlots(8, 84);
    }
    
    @Nonnull
    @Override
    public ItemStack slotClick(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull EntityPlayer player) {
        if (clickType == ClickType.PICKUP && slot < 8 && !player.inventory.getItemStack().isEmpty() && player.inventory.getItemStack().getCount() == 1) {
            ItemStack inMouse = player.inventory.getItemStack();

            if (inMouse.getItem() instanceof ItemBlock) {
                return super.slotClick(slot, dragType, clickType, player);
            }

            this.detectAndSendChanges();
            player.inventory.setItemStack(inMouse);
            return inMouse;
        }
        return super.slotClick(slot, dragType, clickType, player);
    }

    
    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();

            final int inventorySize = 5;
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotbarEnd = playerInventoryEnd + 9;

            if (index < 8) {
                if (!this.mergeItemStack(stack, inventorySize, playerHotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, itemstack);
            } else if (!this.mergeItemStack(stack, 0, 8, false)) {
                    return ItemStack.EMPTY;
            }
            
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return itemstack;
    }

    // We need to override this to prevent shift-clicking items in slots filled with fluids.
    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack) && slot.isItemValid(stack) && (i >= 8 || this.inventory.getFluidInTank(i) != null)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack) && (i >= 8 || this.inventory.getFluidInTank(i) == null)) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.splitStack(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }
    
    private boolean areItemsAndTagsEqual(ItemStack stack, ItemStack itemstack) {
        return ItemStack.areItemsEqual(stack, itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
    }
    
    private ItemStack tryToDepositFluid(IFluidHandlerItem fluidCap) {
        for (int i = 0; i < 8; i++) {
            if (this.inventory.getStackInSlot(i).isEmpty() && this.inventory.getFluidInTank(i) == null) {
                FluidStack maxDrain = fluidCap.drain(this.inventory.getTankCapacity(), false);
                if (this.inventory.isFluidValid(i, maxDrain)) {
                    fluidCap.drain(maxDrain, true);
                    this.inventory.setStackInSlot(i, maxDrain);
                }
            }
        }
        return fluidCap.getContainer();
    }
    
    public static class ItemAndFluidSlot extends SlotItemHandler {
        
        public final TileMechanicalDaisy.InventoryHandler inventory;
        
        public ItemAndFluidSlot(TileMechanicalDaisy.InventoryHandler inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
            this.inventory = inventory;
        }
    }
}
