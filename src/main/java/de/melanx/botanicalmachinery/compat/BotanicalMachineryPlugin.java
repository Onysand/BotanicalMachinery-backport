package de.melanx.botanicalmachinery.compat;

import de.melanx.botanicalmachinery.blocks.screens.*;
import de.melanx.botanicalmachinery.core.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.integration.jei.brewery.BreweryRecipeCategory;
import vazkii.botania.client.integration.jei.elventrade.ElvenTradeRecipeCategory;
import vazkii.botania.client.integration.jei.manapool.ManaPoolRecipeCategory;
import vazkii.botania.client.integration.jei.petalapothecary.PetalApothecaryRecipeCategory;
import vazkii.botania.client.integration.jei.puredaisy.PureDaisyRecipeCategory;
import vazkii.botania.client.integration.jei.runicaltar.RunicAltarRecipeCategory;

@JEIPlugin
public class BotanicalMachineryPlugin implements IModPlugin {
    
    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeClickArea(ScreenAlfheimMarket.class, 77, 36, 22, 15, ElvenTradeRecipeCategory.UID);
        registry.addRecipeClickArea(ScreenMechanicalApothecary.class, 87, 65, 22, 15, PetalApothecaryRecipeCategory.UID);
        registry.addRecipeClickArea(ScreenMechanicalBrewery.class, 96, 48, 22, 15, BreweryRecipeCategory.UID);
        registry.addRecipeClickArea(ScreenMechanicalDaisy.class, 24, 16, 24, 48, PureDaisyRecipeCategory.UID);
        registry.addRecipeClickArea(ScreenMechanicalManaPool.class, 77, 36, 22, 15, ManaPoolRecipeCategory.UID);
        registry.addRecipeClickArea(ScreenMechanicalRunicAltar.class, 87, 65, 22, 15, RunicAltarRecipeCategory.UID);
        
        registry.addRecipeCatalyst(new ItemStack(Registration.BLOCK_ALFHEIM_MARKET), ElvenTradeRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.BLOCK_MECHANICAL_APOTHECARY), PetalApothecaryRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.BLOCK_MECHANICAL_BREWERY), BreweryRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.BLOCK_MECHANICAL_DAISY), PureDaisyRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.BLOCK_MECHANICAL_MANA_POOL), ManaPoolRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.BLOCK_MECHANICAL_RUNIC_ALTAR), RunicAltarRecipeCategory.UID);
    }
}
