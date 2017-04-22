package forestry.core.utils;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class JeiUtil {
	public static void addDescription(IModRegistry registry, String itemKey, Item... items) {
		List<ItemStack> itemStacks = new ArrayList<>();
		for (Item item : items) {
			itemStacks.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
		}

		registry.addDescription(itemStacks, "for.jei.description." + itemKey);
	}

	public static void addDescription(IModRegistry registry, Block... blocks) {
		for (Block block : blocks) {
			Item item = Item.getItemFromBlock(block);
			if (item != Items.AIR) {
				addDescription(registry, item);
			} else {
				Log.error("No item for block {}", block);
			}
		}
	}

	public static void addDescription(IModRegistry registry, Item... items) {
		for (Item item : items) {
			addDescription(registry, item);
		}
	}

	public static void addDescription(IModRegistry registry, Item item) {
		String resourcePath = item.getRegistryName().getResourcePath();
		addDescription(registry, item, resourcePath);
	}

	public static void addDescription(IModRegistry registry, Item item, String itemKey) {
		ItemStack itemStack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
		registry.addDescription(itemStack, "for.jei.description." + itemKey);
	}
}
