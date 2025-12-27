package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.*;
import de.melanx.botanicalmachinery.blocks.base.ContainerBase;
import de.melanx.botanicalmachinery.blocks.containers.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(BotanicalMachinery.MODID)
public class Registration {

    public static final Block BLOCK_MANA_EMERALD = new Block(Material.IRON, MapColor.EMERALD).setHardness(5.0F).setResistance(6.0F).setRegistryName(LibNames.MANA_EMERALD_BLOCK);
    public static final Block BLOCK_ALFHEIM_MARKET = new BlockAlfheimMarket();
    public static final Block BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY = new BlockIndustrialAgglomerationFactory().setRegistryName(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY);
    public static final Block BLOCK_MANA_BATTERY = new BlockManaBattery(BlockManaBattery.Variant.NORMAL).setRegistryName(LibNames.MANA_BATTERY);
    public static final Block BLOCK_MANA_BATTERY_CREATIVE = new BlockManaBattery(BlockManaBattery.Variant.CREATIVE).setRegistryName(LibNames.MANA_BATTERY_CREATIVE);
    public static final Block BLOCK_MECHANICAL_APOTHECARY = new BlockMechanicalApothecary().setRegistryName(LibNames.MECHANICAL_APOTHECARY);
    public static final Block BLOCK_MECHANICAL_BREWERY = new BlockMechanicalBrewery().setRegistryName(LibNames.MECHANICAL_BREWERY);
    public static final Block BLOCK_MECHANICAL_DAISY = new BlockMechanicalDaisy().setRegistryName(LibNames.MECHANICAL_DAISY);
    public static final Block BLOCK_MECHANICAL_MANA_POOL = new BlockMechanicalManaPool().setRegistryName(LibNames.MECHANICAL_MANA_POOL);
    public static final Block BLOCK_MECHANICAL_RUNIC_ALTAR = new BlockMechanicalRunicAltar().setRegistryName(LibNames.MECHANICAL_RUNIC_ALTAR);

    public static final Item ITEM_MANA_EMERALD = new Item().setRegistryName(LibNames.MANA_EMERALD).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MANA_EMERALD_BLOCK = new ItemBlock(BLOCK_MANA_EMERALD).setRegistryName(LibNames.MANA_EMERALD_BLOCK).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_ALFHEIM_MARKET = new ItemBlock(BLOCK_ALFHEIM_MARKET).setRegistryName(LibNames.ALFHEIM_MARKET).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_INDUSTRIAL_AGGLOMERATION_FACTORY = new ItemBlock(BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY).setRegistryName(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MANA_BATTERY = new ItemBlock(BLOCK_MANA_BATTERY).setRegistryName(LibNames.MANA_BATTERY).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MANA_BATTERY_CREATIVE = new ItemBlock(BLOCK_MANA_BATTERY_CREATIVE).setRegistryName(LibNames.MANA_BATTERY_CREATIVE).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MECHANICAL_APOTHECARY = new ItemBlock(BLOCK_MECHANICAL_APOTHECARY).setRegistryName(LibNames.MECHANICAL_APOTHECARY).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MECHANICAL_BREWERY = new ItemBlock(BLOCK_MECHANICAL_BREWERY).setRegistryName(LibNames.MECHANICAL_BREWERY).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MECHANICAL_DAISY = new ItemBlock(BLOCK_MECHANICAL_DAISY).setRegistryName(LibNames.MECHANICAL_DAISY).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MECHANICAL_MANA_POOL = new ItemBlock(BLOCK_MECHANICAL_MANA_POOL).setRegistryName(LibNames.MECHANICAL_MANA_POOL).setCreativeTab(BotanicalMachinery.creativeTab);
    public static final Item ITEM_MECHANICAL_RUNIC_ALTAR = new ItemBlock(BLOCK_MECHANICAL_RUNIC_ALTAR).setRegistryName(LibNames.MECHANICAL_RUNIC_ALTAR).setCreativeTab(BotanicalMachinery.creativeTab);
    
    
    public static final TileEntityType<TileIndustrialAgglomerationFactory> TILE_INDUSTRIAL_AGGLOMERATION_FACTORY = TILES.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> TileEntityType.Builder.create(TileIndustrialAgglomerationFactory::new, BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY).build(null));
    public static final TileEntityType<TileManaBattery> TILE_MANA_BATTERY = TILES.register(LibNames.MANA_BATTERY, () -> TileEntityType.Builder.create(TileManaBattery::new, BLOCK_MANA_BATTERY, BLOCK_MANA_BATTERY_CREATIVE).build(null));
    public static final TileEntityType<TileMechanicalApothecary> TILE_MECHANICAL_APOTHECARY = TILES.register(LibNames.MECHANICAL_APOTHECARY, () -> TileEntityType.Builder.create(TileMechanicalApothecary::new, BLOCK_MECHANICAL_APOTHECARY).build(null));
    public static final TileEntityType<TileMechanicalBrewery> TILE_MECHANICAL_BREWERY = TILES.register(LibNames.MECHANICAL_BREWERY, () -> TileEntityType.Builder.create(TileMechanicalBrewery::new, BLOCK_MECHANICAL_BREWERY).build(null));
    public static final TileEntityType<TileMechanicalDaisy> TILE_MECHANICAL_DAISY = TILES.register(LibNames.MECHANICAL_DAISY, () -> TileEntityType.Builder.create(TileMechanicalDaisy::new, BLOCK_MECHANICAL_DAISY).build(null));
    public static final TileEntityType<TileMechanicalManaPool> TILE_MECHANICAL_MANA_POOL = TILES.register(LibNames.MECHANICAL_MANA_POOL, () -> TileEntityType.Builder.create(TileMechanicalManaPool::new, BLOCK_MECHANICAL_MANA_POOL).build(null));
    public static final TileEntityType<TileMechanicalRunicAltar> TILE_MECHANICAL_RUNIC_ALTAR = TILES.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> TileEntityType.Builder.create(TileMechanicalRunicAltar::new, BLOCK_MECHANICAL_RUNIC_ALTAR).build(null));

    public static final RegistryObject<ContainerType<ContainerAlfheimMarket>> CONTAINER_ALFHEIM_MARKET = CONTAINERS.register(LibNames.ALFHEIM_MARKET, () -> ContainerBase.createContainerType(ContainerAlfheimMarket::new));
    public static final RegistryObject<ContainerType<ContainerIndustrialAgglomerationFactory>> CONTAINER_INDUSTRIAL_AGGLOMERATION_FACTORY = CONTAINERS.register(LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY, () -> ContainerBase.createContainerType(ContainerIndustrialAgglomerationFactory::new));
    public static final RegistryObject<ContainerType<ContainerManaBattery>> CONTAINER_MANA_BATTERY = CONTAINERS.register(LibNames.MANA_BATTERY, () -> ContainerBase.createContainerType(ContainerManaBattery::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalApothecary>> CONTAINER_MECHANICAL_APOTHECARY = CONTAINERS.register(LibNames.MECHANICAL_APOTHECARY, () -> ContainerBase.createContainerType(ContainerMechanicalApothecary::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalBrewery>> CONTAINER_MECHANICAL_BREWERY = CONTAINERS.register(LibNames.MECHANICAL_BREWERY, () -> ContainerBase.createContainerType(ContainerMechanicalBrewery::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalDaisy>> CONTAINER_MECHANICAL_DAISY = CONTAINERS.register(LibNames.MECHANICAL_DAISY, () -> ContainerBase.createContainerType(ContainerMechanicalDaisy::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalManaPool>> CONTAINER_MECHANICAL_MANA_POOL = CONTAINERS.register(LibNames.MECHANICAL_MANA_POOL, () -> ContainerBase.createContainerType(ContainerMechanicalManaPool::new));
    public static final RegistryObject<ContainerType<ContainerMechanicalRunicAltar>> CONTAINER_MECHANICAL_RUNIC_ALTAR = CONTAINERS.register(LibNames.MECHANICAL_RUNIC_ALTAR, () -> ContainerBase.createContainerType(ContainerMechanicalRunicAltar::new));

    public static void init() {
        GameRegistry.registerTileEntity(TileAlfheimMarket.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.ALFHEIM_MARKET));
        GameRegistry.registerTileEntity(TileIndustrialAgglomerationFactory.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY));
        GameRegistry.registerTileEntity(TileManaBattery.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.MANA_BATTERY));
        GameRegistry.registerTileEntity(TileMechanicalApothecary.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.MECHANICAL_APOTHECARY));
        GameRegistry.registerTileEntity(TileMechanicalBrewery.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.MECHANICAL_BREWERY));
        GameRegistry.registerTileEntity(TileMechanicalDaisy.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.MECHANICAL_DAISY));
        GameRegistry.registerTileEntity(TileMechanicalManaPool.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.MECHANICAL_MANA_POOL));
        GameRegistry.registerTileEntity(TileMechanicalRunicAltar.class, new ResourceLocation(BotanicalMachinery.MODID, LibNames.MECHANICAL_RUNIC_ALTAR));
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus);
        BotanicalMachinery.LOGGER.info(ITEMS.getEntries().size() + " items registered.");
        bus);
        BotanicalMachinery.LOGGER.info(BLOCKS.getEntries().size() + " blocks registered.");
        TILES.register(bus);
        BotanicalMachinery.LOGGER.info(TILES.getEntries().size() + " tiles registered.");
        CONTAINERS.register(bus);
        BotanicalMachinery.LOGGER.info(CONTAINERS.getEntries().size() + " containers registered.");
    }
}
