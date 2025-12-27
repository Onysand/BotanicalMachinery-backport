package de.melanx.botanicalmachinery.util.functionalinterface;


import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import net.minecraft.tileentity.TileEntity;

@FunctionalInterface
public interface GuiFactory<C extends ContainerBase<? extends TileEntity>> {
	ScreenBase<C> create(C container);
}
