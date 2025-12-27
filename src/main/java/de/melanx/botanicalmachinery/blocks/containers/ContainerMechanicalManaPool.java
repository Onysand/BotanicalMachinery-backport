package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalManaPool;
import de.melanx.botanicalmachinery.util.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMechanicalManaPool extends ContainerBase<TileMechanicalManaPool> {
    public ContainerMechanicalManaPool(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player) {
        super(world, pos, playerInventory, player, 2, 3);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        // We pass in the catalyst slot at first because it'll be scanned first in transferStackInSlot
        this.addSlotToContainer(new SlotItemHandler(inventory, 1, 53, 25));
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 53, 47));
        this.addSlotToContainer(new SlotOutputOnly(inventory, 2, 111, 37));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
