package de.melanx.botanicalmachinery.gui;

import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.base.ScreenBase;
import de.melanx.botanicalmachinery.blocks.containers.*;
import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.util.functionalinterface.ContainerFactory;
import de.melanx.botanicalmachinery.util.functionalinterface.GuiFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;
import java.util.HashMap;

public class GuiHandler implements IGuiHandler {
	
	public static final int ALFHEIM_MARKET_ID = 0;
	public static final int INDUSTRIAL_AGGLOMERATION_FACTORY_ID = 1;
	public static final int MECHANICAL_DAISY_ID = 2;
	public static final int MECHANICAL_MANA_POOL_ID = 3;
	public static final int MECHANICAL_APOTHECARY_ID = 4;
	public static final int MECHANICAL_BREWERY_ID = 5;
	public static final int MECHANICAL_RUNIC_ALTAR_ID = 6;
	public static final int MANA_BATTERY_ID = 7;
	
	public static final HashMap<Integer, ContainerFactory<?>> SERVER_MAP = new HashMap<>();
	public static final HashMap<Integer, GuiFactory<?>> CLIENT_MAP = new HashMap<>();
	
	static {
		register(ALFHEIM_MARKET_ID, ScreenAlfheimMarket::new, ContainerAlfheimMarket::new);
		register(INDUSTRIAL_AGGLOMERATION_FACTORY_ID, ScreenIndustrialAgglomerationFactory::new, ContainerIndustrialAgglomerationFactory::new);
		register(MECHANICAL_DAISY_ID, ScreenMechanicalDaisy::new, ContainerMechanicalDaisy::new);
		register(MECHANICAL_MANA_POOL_ID, ScreenMechanicalManaPool::new, ContainerMechanicalManaPool::new);
		register(MECHANICAL_APOTHECARY_ID, ScreenMechanicalApothecary::new, ContainerMechanicalApothecary::new);
		register(MECHANICAL_BREWERY_ID, ScreenMechanicalBrewery::new, ContainerMechanicalBrewery::new);
		register(MECHANICAL_RUNIC_ALTAR_ID, ScreenMechanicalRunicAltar::new, ContainerMechanicalRunicAltar::new);
		register(MANA_BATTERY_ID, ScreenManaBattery::new, ContainerManaBattery::new);
	}
	
	private static <S extends ContainerBase<? extends TileEntity>, C extends TileEntity> void register(
		int id,
		GuiFactory<S> screenFactory,
		ContainerFactory<C> containerFactory) {
		
		CLIENT_MAP.put(id, screenFactory);
		SERVER_MAP.put(id, containerFactory);
	}
	
	@Nullable
	@Override
	public ContainerBase<?> getServerGuiElement(int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		ContainerFactory<?> factory = SERVER_MAP.get(id);
		if (factory == null) return null;
		return factory.create(world, new BlockPos(x, y, z), entityPlayer.inventory, entityPlayer);
	}
	
	@Nullable
	@Override
	public ScreenBase<?> getClientGuiElement(int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		ContainerBase<?> container = getServerGuiElement(id, entityPlayer, world, x, y, z);
		if (container instanceof ContainerBase) {
			// noinspection rawtypes
			GuiFactory factory = CLIENT_MAP.get(id);
			if (factory == null) return null;
			// noinspection unchecked
			return factory.create(container);
		}
		return null;
	}
}
