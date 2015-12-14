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
package forestry.core.recipes.nei;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import forestry.core.config.Constants;

import codechicken.nei.api.ItemFilter;

/**
 * @author bdew
 */
public class ItemFilterForestry implements ItemFilter {
	private final boolean items;

	public ItemFilterForestry(Boolean items) {
		this.items = items;
	}

	@Override
	public boolean matches(ItemStack item) {
		if (item == null || item.getItem() == null) {
			return false;
		}

		if (item.getItem() instanceof ItemBlock && items) {
			return false;
		}

		if (!(item.getItem() instanceof ItemBlock) && !items) {
			return false;
		}

		String itemName = Item.itemRegistry.getNameForObject(item.getItem());
		if (itemName == null) {
			return false;
		}

		String[] s = itemName.split(":");
		if (s.length <= 1) {
			return false;
		}

		return s[0].equals(Constants.MOD);
	}
}
