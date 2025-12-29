package de.melanx.botanicalmachinery.config;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BotanicalMachinery.MODID)
public class ConfigEvents {
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(BotanicalMachinery.MODID)) {
			ConfigManager.sync(BotanicalMachinery.MODID, Config.Type.INSTANCE);
		}
	}
}
