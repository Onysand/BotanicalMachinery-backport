package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalBrewery;
import de.melanx.botanicalmachinery.util.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMechanicalBrewery extends ContainerBase<TileMechanicalBrewery> {
    public ContainerMechanicalBrewery(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player) {
        super(world, pos, playerInventory, player, 7, 8);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 44, 48));
        this.addSlotToContainer(new SlotItemHandler(inventory, 1, 29, 18));
        this.addSlotToContainer(new SlotItemHandler(inventory, 2, 59, 18));
        this.addSlotToContainer(new SlotItemHandler(inventory, 3, 14, 48));
        this.addSlotToContainer(new SlotItemHandler(inventory, 4, 74, 48));
        this.addSlotToContainer(new SlotItemHandler(inventory, 5, 29, 78));
        this.addSlotToContainer(new SlotItemHandler(inventory, 6, 59, 78));
        this.addSlotToContainer(new SlotOutputOnly(inventory, 7, 128, 49));
        this.layoutPlayerInventorySlots(8, 110);
    }
}
