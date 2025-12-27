package de.melanx.botanicalmachinery.config;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class ClientConfig {
    
    public static boolean numericalMana;
    public static boolean everything;
    public static boolean alfheimMarket;
    public static boolean agglomerationFactory;
    public static boolean apothecary;
    public static boolean brewery;
    public static boolean daisy;
    public static boolean manaPool;
    public static boolean runicAltar;
    
    private static Configuration config;
    
    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
    }
    
    public static void syncConfig() {
        try {
            config.load();
            
            numericalMana = config.getBoolean("numericalMana", Configuration.CATEGORY_GENERAL, true, "Should mana in GUIs be displayed with numbers?");
            
            String category = "advanced-rendering";
            config.setCategoryComment(category, "Should the machine render its specific rendering if items are in the machine?");
            
            everything = config.getBoolean("all", category, true, "If you turn this off, the special rendering is disabled for all machines and ignores the other config options");
            alfheimMarket = config.getBoolean("alfheim-market", category, true, "Enable special rendering for Alfheim Market");
            agglomerationFactory = config.getBoolean("industrial-agglomeration-factory", category, true, "Enable special rendering for Agglomeration Factory");
            apothecary = config.getBoolean("mechanical-apothecary", category, true, "Enable special rendering for Apothecary");
            brewery = config.getBoolean("mechanical-brewery", category, true, "Enable special rendering for Brewery");
            daisy = config.getBoolean("mechanical-daisy", category, true, "Enable special rendering for Daisy");
            manaPool = config.getBoolean("mechanical-mana-pool", category, true, "Enable special rendering for Mana Pool");
            runicAltar = config.getBoolean("mechanical-runic-altar", category, true, "Enable special rendering for Runic Altar");
            
        } catch (Exception e) {
             BotanicalMachinery.LOGGER.error("Could not load client config!", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}