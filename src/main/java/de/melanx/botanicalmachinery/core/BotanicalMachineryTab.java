package de.melanx.botanicalmachinery.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.ModItems;

public class BotanicalMachineryTab extends CreativeTabs {
    public BotanicalMachineryTab(String label) {
        super(label);
    }
    
    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.auraRing);
    }
}
