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
package forestry.food.items;

import forestry.core.items.ItemForestryFood;
import forestry.core.items.ItemRegistry;

public class ItemRegistryFood extends ItemRegistry {
	public final ItemForestryFood honeyedSlice;
	public final ItemForestryFood ambrosia;
	public final ItemForestryFood honeyPot;

	public ItemRegistryFood() {
		// / FOOD ITEMS
		honeyedSlice = registerItem(new ItemForestryFood(8, 0.6f), "honeyed_slice");
		ambrosia = registerItem(new ItemAmbrosia().setIsDrink(), "ambrosia");
		honeyPot = registerItem(new ItemForestryFood(2, 0.2f).setIsDrink(), "honey_pot");
	}
}
