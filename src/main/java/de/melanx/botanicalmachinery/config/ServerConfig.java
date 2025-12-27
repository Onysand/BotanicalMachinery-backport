package de.melanx.botanicalmachinery.config;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class ServerConfig {
    public static Configuration config;
    
    // Multipliers
    public static int multiplierAlfheimMarket;
    public static int multiplierAgglomerationFactory;
    public static int multiplierManaPool;
    public static int multiplierRunicAltar;
    public static int multiplierDaisy;
    public static int multiplierBrewery;
    public static int multiplierApothecary;
    
    // Recipe Costs
    public static int alfheimMarketRecipeCost;
    
    // Capacities
    public static int capacityAlfheimMarket;
    public static int capacityAgglomerationFactory;
    public static int capacityManaPool;
    public static int capacityRunicAltar;
    public static int capacityBrewery;
    public static int capacityManaBattery;
    
    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
    }
    
    public static void syncConfig() {
        try {
            config.load();
            
            // --- Working Duration Multipliers ---
            String catMultipliers = "working-duration-multiplier";
            config.setCategoryComment(catMultipliers, "The default duration multiplied with this will be the used working duration.");
            
            multiplierAlfheimMarket = config.getInt("alfheim-market", catMultipliers, 1, 1, Integer.MAX_VALUE, "");
            multiplierAgglomerationFactory = config.getInt("industrial-agglomeration-factory", catMultipliers, 1, 1, Integer.MAX_VALUE, "");
            multiplierManaPool = config.getInt("mechanical-mana-pool", catMultipliers, 1, 1, Integer.MAX_VALUE, "");
            multiplierRunicAltar = config.getInt("mechanical-runic-altar", catMultipliers, 1, 1, Integer.MAX_VALUE, "");
            multiplierDaisy = config.getInt("mechanical-daisy", catMultipliers, 3, 1, Integer.MAX_VALUE, "");
            multiplierBrewery = config.getInt("mechanical-brewery", catMultipliers, 1, 1, Integer.MAX_VALUE, "");
            multiplierApothecary = config.getInt("mechanical-apothecary", catMultipliers, 1, 1, Integer.MAX_VALUE, "");
            
            // --- Recipe Costs ---
            alfheimMarketRecipeCost = config.getInt("recipe-cost", "alfheim-market", 500, 1, Integer.MAX_VALUE, "The amount of mana used in alfheim market to trade items");
            
            // --- Max Mana Capacity ---
            String catCapacity = "max-mana-capacity";
            config.setCategoryComment(catCapacity, "The default amount of mana capacity in each machine.");
            
            capacityAlfheimMarket = config.getInt("alfheim-market", catCapacity, 100000, 1, Integer.MAX_VALUE, "");
            capacityAgglomerationFactory = config.getInt("industrial-agglomeration-factory", catCapacity, 1000000, 500000, Integer.MAX_VALUE, "");
            capacityManaPool = config.getInt("mechanical-mana-pool", catCapacity, 100000, 1, Integer.MAX_VALUE, "");
            capacityRunicAltar = config.getInt("mechanical-runic-altar", catCapacity, 250000, 1, Integer.MAX_VALUE, "");
            capacityBrewery = config.getInt("mechanical-brewery", catCapacity, 100000, 1, Integer.MAX_VALUE, "");
            capacityManaBattery = config.getInt("mana-battery", catCapacity, 10000000, 1, Integer.MAX_VALUE, "");
            
        } catch (Exception e) {
             BotanicalMachinery.LOGGER.error("Failed to load server config", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}