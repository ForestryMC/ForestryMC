/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.storage;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

import forestry.api.core.IItemProvider;
import forestry.api.storage.ICrateRegistry;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.items.ItemCrated;

public class CrateRegistry implements ICrateRegistry {

	private static void registerCrate(Item item, @Nullable String tagName) {
		registerCrate(new ItemStack(item), tagName);
	}

	private static void registerCrate(ItemStack stack, @Nullable String tagName) {
		if (stack.isEmpty()) {
			Log.error("Tried to make a crate without an item");
			return;
		}

		String crateName;
		String identifier;
		if (tagName != null) {
			crateName = "crated_" + tagName;
			identifier = tagName;
		} else {
			String stringForItemStack = ItemStackUtil.getStringForItemStack(stack);
			if (stringForItemStack == null) {
				Log.error("Could not get string name for itemStack {}", stack);
				return;
			}
			String itemName = stringForItemStack.replace(':', '_');
			itemName = itemName.replace("minecraft_", "");
			crateName = "crated_" + itemName;
			identifier = itemName;
		}

		IFeatureRegistry registry = ModFeatureRegistry.get(ModuleCrates.class);
		ModuleCrates.registerCrate(registry.item(() -> new ItemCrated(stack, tagName, identifier), crateName));
	}

	@Override
	public void registerCrate(String oreDictName) {
		if (ModuleCrates.cratesRejectedOreDict.contains(oreDictName)) {
			return;
		}
		//		if (OreDictionary.doesOreNameExist(oreDictName)) {
		//			for (ItemStack stack : OreDictionary.getOres(oreDictName)) {
		//				if (stack != null) {
		//					registerCrate(stack, oreDictName);
		//					break;
		//				}
		//			}
		//		}	//TODO tags
	}

	@Override
	public void registerCrate(ITag<Item> tag) {
		ResourceLocation location = TagCollectionManager.getInstance().getItems().getId(tag);
		if (location == null) {
			return;
		}
		//TagCollectionManager.getInstance().getItems().getTag(tag_name)
		/*for (Item item : tag.getValues()) {
			if (item != null) {
				registerCrate(item, location.toString().replace(":", "_"));
				break;
			}
		}*/
	}

	@Override
	public void registerCrate(Block block) {
		registerCrate(new ItemStack(block), null);
	}

	@Override
	public void registerCrate(Item item) {
		registerCrate(new ItemStack(item), null);
	}

	@Override
	public void registerCrate(IItemProvider provider) {
		if (provider.hasItem()) {
			registerCrate(provider.item());
		}
	}

	@Override
	public void registerCrate(ItemStack stack) {
		Collection<ItemStack> testStacks = ModuleCrates.cratesRejectedItem.get(stack.getItem());
		for (ItemStack testStack : testStacks) {
			if (ItemStackUtil.areItemStacksEqualIgnoreCount(stack, testStack)) {
				return;
			}
		}
		registerCrate(stack, null);
	}

	@Override
	public void blacklistCrate(ItemStack stack) {
		ModuleCrates.cratesRejectedItem.put(stack.getItem(), stack);
	}
}
