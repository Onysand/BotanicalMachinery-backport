package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.tesr.*;
import de.melanx.botanicalmachinery.blocks.tiles.*;
import de.melanx.botanicalmachinery.core.BotanicalMachineryTab;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = BotanicalMachinery.MODID,
    dependencies = "required-after:forge@[14.23.5.2864,);required-after:baubles@[1.5.2,);required-after:botania@[r1.10-364,);"
)
public class BotanicalMachinery {
    
    public static final String MODID = "botanicalmachinery";
    public static final CreativeTabs creativeTab = new BotanicalMachineryTab(MODID);
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    
    @Mod.Instance(MODID)
    public static BotanicalMachinery instance;

    public BotanicalMachinery() {}
    
    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        BotanicalMachineryNetwork.registerPackets();
        MinecraftForge.EVENT_BUS.register(Registration.class);
    }

    @Mod.EventHandler
    private void init(final FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(BotanicalMachinery.MODID, new GuiHandler());

        ClientRegistry.bindTileEntitySpecialRenderer(TileMechanicalDaisy.class, new TesrMechanicalDaisy());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAlfheimMarket.class, new TesrAlfheimMarket());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMechanicalManaPool.class, new TesrMechanicalManaPool());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMechanicalRunicAltar.class, new TesrMechanicalRunicAltar());
        ClientRegistry.bindTileEntitySpecialRenderer(TileIndustrialAgglomerationFactory.class, new TesrIndustrialAgglomerationFactory());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMechanicalApothecary.class, new TesrMechanicalApothecary());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMechanicalBrewery.class, new TesrMechanicalBrewery());
    }
}
