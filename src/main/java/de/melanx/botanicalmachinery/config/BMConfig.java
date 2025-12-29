package de.melanx.botanicalmachinery.config;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraftforge.common.config.Config;

@Config(modid = BotanicalMachinery.MODID)
public class BMConfig {
	
	@Config.Name("Client")
	public static final Client CLIENT = new Client();
	
	@Config.Name("Server")
	public static final Server SERVER = new Server();
	
	// ================= CLIENT =================
	
	public static class Client {
		
		@Config.Comment("Should mana in GUIs be displayed with numbers?")
		public boolean numericalMana = true;
		
		@Config.Name("Advanced Rendering")
		@Config.Comment("Special item rendering inside machines")
		public AdvancedRendering rendering = new AdvancedRendering();
		
		public static class AdvancedRendering {
			
			@Config.Comment("Master switch for all special rendering")
			public boolean all = true;
			
			public boolean alfheimMarket = true;
			public boolean agglomerationFactory = true;
			public boolean apothecary = true;
			public boolean brewery = true;
			public boolean daisy = true;
			public boolean manaPool = true;
			public boolean runicAltar = true;
		}
	}
	
	// ================= SERVER =================
	
	public static class Server {
		
		@Config.Name("Working Duration Multipliers")
		public Multipliers multipliers = new Multipliers();
		
		@Config.Name("Recipe Costs")
		public RecipeCosts recipeCosts = new RecipeCosts();
		
		@Config.Name("Mana Capacities")
		public Capacities capacities = new Capacities();
		
		public static class Multipliers {
			
			@Config.RangeInt(min = 1)
			public int alfheimMarket = 1;
			
			@Config.RangeInt(min = 1)
			public int agglomerationFactory = 1;
			
			@Config.RangeInt(min = 1)
			public int manaPool = 1;
			
			@Config.RangeInt(min = 1)
			public int runicAltar = 1;
			
			@Config.RangeInt(min = 1)
			public int daisy = 3;
			
			@Config.RangeInt(min = 1)
			public int brewery = 1;
			
			@Config.RangeInt(min = 1)
			public int apothecary = 1;
		}
		
		public static class RecipeCosts {
			
			@Config.RangeInt(min = 1)
			@Config.Comment("Mana used by Alfheim Market per trade")
			public int alfheimMarket = 500;
		}
		
		public static class Capacities {
			
			@Config.RangeInt(min = 1)
			public int alfheimMarket = 100_000;
			
			@Config.RangeInt(min = 500_000)
			public int agglomerationFactory = 1_000_000;
			
			@Config.RangeInt(min = 1)
			public int manaPool = 100_000;
			
			@Config.RangeInt(min = 1)
			public int runicAltar = 250_000;
			
			@Config.RangeInt(min = 1)
			public int brewery = 100_000;
			
			@Config.RangeInt(min = 1)
			public int manaBattery = 10_000_000;
			
			@Config.RangeInt(min = 1_000)
			public int fluidDaisy = 10_000;
		}
	}
}
