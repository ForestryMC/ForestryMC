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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

import forestry.api.storage.ICrateRegistry;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.items.ItemCrated;

public class CrateRegistry implements ICrateRegistry {

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
	public void registerCrate(Item item) {
		registerCrate(new ItemStack(item));
	}

	@Override
	public void registerCrate(ItemStack stack) {
		if (stack.isEmpty()) {
			Log.error("Tried to make a crate without an item");
			return;
		}

		String stringForItemStack = ItemStackUtil.getStringForItemStack(stack);

		if (stringForItemStack == null) {
			Log.error("Could not get string name for itemStack {}", stack);
			return;
		}

		String crateName = "crated/" + stringForItemStack.replace(':', '/');

		IFeatureRegistry registry = ModFeatureRegistry.get(ModuleCrates.class);
		ModuleCrates.registerCrate(registry.item(() -> new ItemCrated(stack), crateName));
	}
}
