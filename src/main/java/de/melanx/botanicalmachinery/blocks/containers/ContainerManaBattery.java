package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerManaBattery extends ContainerBase<TileManaBattery> {
    public ContainerManaBattery(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player) {
        super(world, pos, playerInventory, player, 2, 2);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 53, 25));
        this.addSlotToContainer(new SlotItemHandler(inventory, 1, 107, 25));
        this.layoutPlayerInventorySlots(8, 84);
    }
}
