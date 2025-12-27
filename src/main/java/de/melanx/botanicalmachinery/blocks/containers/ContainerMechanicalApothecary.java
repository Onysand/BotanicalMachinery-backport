package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.util.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMechanicalApothecary extends ContainerBase<TileMechanicalApothecary> {
    public ContainerMechanicalApothecary(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player) {
        super(world, pos, playerInventory, player, 17, 21);
        IItemHandlerModifiable inventory = this.tile.getInventory().getUnrestricted();
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 90, 43));
        int index = this.addSlotBox(inventory, 1, 8, 26, 4, 18, 4, 18);
        this.addSlotBox(inventory, index, 118, 54, 2, 18, 2, 18, SlotOutputOnly::new);
        this.layoutPlayerInventorySlots(18, 113);
    }
}
