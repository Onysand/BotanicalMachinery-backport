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
		
		if (input instanceof String)
			return isOreDictMatch(input.toString(), stack);
		
		if (input instanceof List) {
			return ((List<?>) input).stream().anyMatch(id -> isInputMatch(id, stack));
		}
		
		return false;
	}
	
	public static boolean isOreDictMatch(String input, ItemStack stack) {
		int targetId = OreDictionary.getOreID(input);
		
		int[] stackIds = OreDictionary.getOreIDs(stack);
		return Arrays.stream(stackIds).anyMatch(id -> id == targetId);
	}
	
	public static Optional<List<ItemStack>> isInputsMatch(List<Object> inputs, List<ItemStack> stacks) {
		List<Object> inputsMissing = new ArrayList<>(inputs);
		List<ItemStack> stacksToRemove = new ArrayList<>();
		
		for(ItemStack stack : stacks) {
			if(stack.isEmpty()) {
				continue;
			}
			if(inputsMissing.isEmpty())
				break;
			
			int stackIndex = -1;
			
			for (int i = 0; i < inputsMissing.size(); i++) {
				Object input = inputsMissing.get(i);
				if (isInputMatch(input, stack)) {
					if(!stacksToRemove.contains(stack))
						stacksToRemove.add(stack);
					stackIndex = i;
					break;
				}
			}
			
			if(stackIndex != -1)
				inputsMissing.remove(stackIndex);
		}
		
		return inputsMissing.isEmpty() ? Optional.of(stacksToRemove) : Optional.empty();
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
