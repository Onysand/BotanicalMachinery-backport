package de.melanx.botanicalmachinery.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * Thanks to Cucumber by BlakeBr0
 * https://github.com/BlakeBr0/Cucumber/blob/1.15/src/main/java/com/blakebr0/cucumber/inventory/SidedItemStackHandlerWrapper.java
 */
public class ItemStackHandlerWrapper implements IItemHandlerModifiable {
    private final IItemHandlerModifiable inventory;
    private final BiFunction<Integer, ItemStack, Boolean> canInsert;
    private final Function<Integer, Boolean> canExtract;

    public ItemStackHandlerWrapper(IItemHandlerModifiable inventory, @Nullable Function<Integer, Boolean> canExtract, @Nullable BiFunction<Integer, ItemStack, Boolean> canInsert) {
        this.inventory = inventory;
        this.canExtract = canExtract;
        this.canInsert = canInsert;
    }
    
    private IItemHandlerModifiable getInv() {
        IItemHandlerModifiable inv = this.inventory;
        if (inv == null) {
            return new ItemStackHandler(0);
        }
        
        return inv;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        this.getInv().setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.getInv().getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.getInv().getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!this.isItemValid(slot, stack)) return stack;
        return this.getInv().insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.canExtract != null && !this.canExtract.apply(slot))
            return ItemStack.EMPTY;

        return this.getInv().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getInv().getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.canInsert == null || this.canInsert.apply(slot, stack);
    }

    public static IItemHandlerModifiable create(IItemHandlerModifiable inv) {
        return new ItemStackHandlerWrapper(inv, null, null);
    }

    public static IItemHandlerModifiable create(IItemHandlerModifiable inv, @Nullable Function<Integer, Boolean> canExtract, @Nullable BiFunction<Integer, ItemStack, Boolean> canInsert) {
        return new ItemStackHandlerWrapper(inv, canExtract, canInsert);
    }

    public static IItemHandlerModifiable createFromSup(Supplier<IItemHandlerModifiable> inv) {
        return new ItemStackHandlerWrapper(inv.get(), null, null);
    }

    public static IItemHandlerModifiable createFromSup(Supplier<IItemHandlerModifiable> inv, @Nullable Function<Integer, Boolean> canExtract, @Nullable BiFunction<Integer, ItemStack, Boolean> canInsert) {
        return new ItemStackHandlerWrapper(inv.get(), canExtract, canInsert);
    }
}
