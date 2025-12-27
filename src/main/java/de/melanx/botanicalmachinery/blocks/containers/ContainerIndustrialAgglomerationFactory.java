package de.melanx.botanicalmachinery.blocks.containers;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.TileBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileIndustrialAgglomerationFactory;
import de.melanx.botanicalmachinery.util.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIndustrialAgglomerationFactory extends ContainerBase<TileIndustrialAgglomerationFactory> {
    public ContainerIndustrialAgglomerationFactory(World world, BlockPos pos, InventoryPlayer playerInventory, EntityPlayer player) {
        super( world, pos, playerInventory, player, 3, 4);
        IItemHandlerModifiable inventory = ((TileBase) this.tile).getInventory().getUnrestricted();
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 61, 83));
        this.addSlotToContainer(new SlotItemHandler(inventory, 1, 80, 83));
        this.addSlotToContainer(new SlotItemHandler(inventory, 2, 99, 83));
        this.addSlotToContainer(new SlotOutputOnly(inventory, 3, 80, 25));
        this.layoutPlayerInventorySlots(8, 113);
    }
}
