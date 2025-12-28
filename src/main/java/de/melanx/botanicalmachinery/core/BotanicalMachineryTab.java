package de.melanx.botanicalmachinery.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class BotanicalMachineryTab extends CreativeTabs {
    public BotanicalMachineryTab(String label) {
        super(label);
    }
    
    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Registration.ITEM_MECHANICAL_MANA_POOL);
    }
}
