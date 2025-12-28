package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BotanicalMachinery.MODID, value = Side.CLIENT)
public class ClientRegistration {
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Block block : Registration.BLOCKS) {
			Item item = Item.getItemFromBlock(block);
			ModelLoader.setCustomModelResourceLocation(item, 0,
				new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}
		
		for (Item item : Registration.ITEMS) {
			ModelLoader.setCustomModelResourceLocation(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}
}