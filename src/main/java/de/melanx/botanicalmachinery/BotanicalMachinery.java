package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.blocks.tesr.*;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import de.melanx.botanicalmachinery.config.ClientConfig;
import de.melanx.botanicalmachinery.config.ServerConfig;
import de.melanx.botanicalmachinery.core.BotanicalMachineryTab;
import de.melanx.botanicalmachinery.core.Registration;
import de.melanx.botanicalmachinery.gui.GuiHandler;
import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = BotanicalMachinery.MODID)
public class BotanicalMachinery {
    
    public static final String MODID = "botanicalmachinery";
    public static final CreativeTabs creativeTab = new BotanicalMachineryTab(MODID);
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    
    @Mod.Instance(MODID)
    public static BotanicalMachinery instance;

    public BotanicalMachinery() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
        ClientConfig.loadConfig(ClientConfig.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
        ServerConfig.loadConfig(ServerConfig.SERVER_CONFIG, FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(MODID + "-server.toml"));
        Registration.init();
    }
    
    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        switch (event.getSide()) {
            case CLIENT:
                ClientConfig.init(new File(event.getModConfigurationDirectory(), MODID + "-client.toml"));
                break;
            case SERVER:
                ServerConfig.init(new File(event.getModConfigurationDirectory(), MODID + "-server.toml"));
                break;
        }
        BotanicalMachineryNetwork.registerPackets();
    }

    @Mod.EventHandler
    private void init(final FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(BotanicalMachinery.MODID, new GuiHandler());

        ClientRegistry.bindTileEntitySpecialRenderer(TileMechanicalDaisy.class, TesrMechanicalDaisy::new);
        ClientRegistry.bindTileEntitySpecialRenderer(Registration.TILE_ALFHEIM_MARKET, TesrAlfheimMarket::new);
        ClientRegistry.bindTileEntitySpecialRenderer(Registration.TILE_MECHANICAL_MANA_POOL, TesrMechanicalManaPool::new);
        ClientRegistry.bindTileEntitySpecialRenderer(Registration.TILE_MECHANICAL_RUNIC_ALTAR, TesrMechanicalRunicAltar::new);
        ClientRegistry.bindTileEntitySpecialRenderer(Registration.TILE_INDUSTRIAL_AGGLOMERATION_FACTORY, TesrIndustrialAgglomerationFactory::new);
        ClientRegistry.bindTileEntitySpecialRenderer(Registration.TILE_MECHANICAL_APOTHECARY, TesrMechanicalApothecary::new);
        ClientRegistry.bindTileEntitySpecialRenderer(Registration.TILE_MECHANICAL_BREWERY, TesrMechanicalBrewery::new);
    }
}
