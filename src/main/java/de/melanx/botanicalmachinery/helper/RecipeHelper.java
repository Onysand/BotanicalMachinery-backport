package de.melanx.botanicalmachinery.helper;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public class RecipeHelper {
	
	public static boolean isInputMatch(Object input, ItemStack stack) {
		if (stack.isEmpty() || input == null) return false;
		
		if (input instanceof ItemStack) {
			ItemStack recipeStack = (ItemStack) input;
			return ItemStack.areItemsEqual(stack, recipeStack);
		}
		
		if (input instanceof String) {
			String targetOre = (String) input;
			int targetId = OreDictionary.getOreID(targetOre);
			
			int[] stackIds = OreDictionary.getOreIDs(stack);
			return Arrays.stream(stackIds).anyMatch(id -> id == targetId);
		}
		
		if (input instanceof List) {
			return ((List<?>) input).stream().anyMatch(id -> isInputMatch(id, stack));
		}
		
		return false;
	}

    /**
     * @param stacks All {@link ItemStack}s from the inventory
     * @return {@link Map} which includes the item and amount for all items in inventory
     */
    public static Map<Item, Integer> getInvItems(List<ItemStack> stacks) {
        Map<Item, Integer> items = new HashMap<>();
        stacks.removeIf(stack -> stack.getItem() == Items.AIR);
        stacks.forEach(stack -> {
            Item item = stack.getItem();
            if (!items.containsKey(item)) {
                items.put(item, stack.getCount());
            } else {
                int prevCount = items.get(item);
                items.replace(item, prevCount, prevCount + stack.getCount());
            }
        });
        return items;
    }

    /**
     * @param list   {@link List} to remove from
     * @param arrays indexes to remove
     */
    public static void removeFromList(List<?> list, int[]... arrays) {
        List<Integer> toRemove = new ArrayList<>();
        for (int[] array : arrays) {
            for (int i : array) {
                toRemove.add(i);
            }
        }
        toRemove.sort(Comparator.naturalOrder());
        for (int i : Lists.reverse(toRemove)) {
            list.remove(i);
        }
    }
}
