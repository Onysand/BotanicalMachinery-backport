package de.melanx.botanicalmachinery.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BotanicalMachineryTab extends CreativeTabs {
    public BotanicalMachineryTab(String label) {
        super(label);
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(Registration.ITEM_MECHANICAL_MANA_POOL);
    }
}
