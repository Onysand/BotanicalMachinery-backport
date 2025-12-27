package de.melanx.botanicalmachinery.util.functionalinterface;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface ContainerFactory<T extends TileEntity> {
	ContainerBase<T> create(World world, BlockPos pos, InventoryPlayer inventoryPlayer, EntityPlayer player);
}
