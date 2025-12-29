package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class Registration {
    
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> ITEMS = new ArrayList<>();
    
    public static final Block BLOCK_MANA_EMERALD = registerBlock(new Block(Material.IRON, MapColor.EMERALD).setHardness(5.0F).setResistance(6.0F), LibNames.MANA_EMERALD_BLOCK);
    public static final Block BLOCK_ALFHEIM_MARKET = registerBlock(new BlockAlfheimMarket(), LibNames.ALFHEIM_MARKET);
    public static final Block BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY = registerBlock(new BlockIndustrialAgglomerationFactory(), LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY);
    public static final Block BLOCK_MANA_BATTERY = registerBlock(new BlockManaBattery(BlockManaBattery.Variant.NORMAL), LibNames.MANA_BATTERY); // Предположим Variant - это boolean
    public static final Block BLOCK_MANA_BATTERY_CREATIVE = registerBlock(new BlockManaBattery(BlockManaBattery.Variant.CREATIVE), LibNames.MANA_BATTERY_CREATIVE);
    public static final Block BLOCK_MECHANICAL_APOTHECARY = registerBlock(new BlockMechanicalApothecary(), LibNames.MECHANICAL_APOTHECARY);
    public static final Block BLOCK_MECHANICAL_BREWERY = registerBlock(new BlockMechanicalBrewery(), LibNames.MECHANICAL_BREWERY);
    public static final Block BLOCK_MECHANICAL_DAISY = registerBlock(new BlockMechanicalDaisy(), LibNames.MECHANICAL_DAISY);
    public static final Block BLOCK_MECHANICAL_MANA_POOL = registerBlock(new BlockMechanicalManaPool(), LibNames.MECHANICAL_MANA_POOL);
    public static final Block BLOCK_MECHANICAL_RUNIC_ALTAR = registerBlock(new BlockMechanicalRunicAltar(), LibNames.MECHANICAL_RUNIC_ALTAR);
    
    public static final Item ITEM_MANA_EMERALD = registerItem(new Item(), LibNames.MANA_EMERALD);
    public static final Item ITEM_MANA_EMERALD_BLOCK = registerItem(new ItemBlock(BLOCK_MANA_EMERALD), LibNames.MANA_EMERALD_BLOCK);
    public static final Item ITEM_ALFHEIM_MARKET = registerItem(new ItemBlock(BLOCK_ALFHEIM_MARKET), LibNames.ALFHEIM_MARKET);
    public static final Item ITEM_INDUSTRIAL_AGGLOMERATION_FACTORY = registerItem(new ItemBlock(BLOCK_INDUSTRIAL_AGGLOMERATION_FACTORY), LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY);
    public static final Item ITEM_MANA_BATTERY = registerItem(new ItemBlock(BLOCK_MANA_BATTERY), LibNames.MANA_BATTERY);
    public static final Item ITEM_MANA_BATTERY_CREATIVE = registerItem(new ItemBlock(BLOCK_MANA_BATTERY_CREATIVE), LibNames.MANA_BATTERY_CREATIVE);
    public static final Item ITEM_MECHANICAL_APOTHECARY = registerItem(new ItemBlock(BLOCK_MECHANICAL_APOTHECARY), LibNames.MECHANICAL_APOTHECARY);
    public static final Item ITEM_MECHANICAL_BREWERY = registerItem(new ItemBlock(BLOCK_MECHANICAL_BREWERY), LibNames.MECHANICAL_BREWERY);
    public static final Item ITEM_MECHANICAL_DAISY = registerItem(new ItemBlock(BLOCK_MECHANICAL_DAISY), LibNames.MECHANICAL_DAISY);
    public static final Item ITEM_MECHANICAL_MANA_POOL = registerItem(new ItemBlock(BLOCK_MECHANICAL_MANA_POOL), LibNames.MECHANICAL_MANA_POOL);
    public static final Item ITEM_MECHANICAL_RUNIC_ALTAR = registerItem(new ItemBlock(BLOCK_MECHANICAL_RUNIC_ALTAR), LibNames.MECHANICAL_RUNIC_ALTAR);
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
        registerTileEntities();
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
    }
    
    private static Block registerBlock(Block block, String name) {
        block.setRegistryName(BotanicalMachinery.MODID, name);
        block.setUnlocalizedName(BotanicalMachinery.MODID + "." + name);
        block.setCreativeTab(BotanicalMachinery.creativeTab);
        BLOCKS.add(block);
        return block;
    }
    
    private static Item registerItem(Item item, String name) {
        item.setRegistryName(BotanicalMachinery.MODID, name);
        item.setUnlocalizedName(BotanicalMachinery.MODID + "." + name);
        item.setCreativeTab(BotanicalMachinery.creativeTab);
        ITEMS.add(item);
        return item;
    }
    
    private static void registerTileEntities() {
        registerTE(TileAlfheimMarket.class, LibNames.ALFHEIM_MARKET);
        registerTE(TileIndustrialAgglomerationFactory.class, LibNames.INDUSTRIAL_AGGLOMERATION_FACTORY);
        registerTE(TileManaBattery.class, LibNames.MANA_BATTERY);
        registerTE(TileMechanicalApothecary.class, LibNames.MECHANICAL_APOTHECARY);
        registerTE(TileMechanicalBrewery.class, LibNames.MECHANICAL_BREWERY);
        registerTE(TileMechanicalDaisy.class, LibNames.MECHANICAL_DAISY);
        registerTE(TileMechanicalManaPool.class, LibNames.MECHANICAL_MANA_POOL);
        registerTE(TileMechanicalRunicAltar.class, LibNames.MECHANICAL_RUNIC_ALTAR);
    }
    
    private static void registerTE(Class<? extends TileEntity> tileClass, String name) {
        GameRegistry.registerTileEntity(tileClass, new ResourceLocation(BotanicalMachinery.MODID, name));
    }
}
