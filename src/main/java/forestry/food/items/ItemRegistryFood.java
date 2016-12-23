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

import java.util.Collections;

import forestry.core.items.ItemForestryFood;
import forestry.core.items.ItemRegistry;
import forestry.food.BeverageEffect;
import forestry.food.BeverageEffects;
import net.minecraft.item.ItemStack;

public class ItemRegistryFood extends ItemRegistry {
	public final ItemForestryFood honeyedSlice;
	public final ItemBeverage beverage;
	public final ItemForestryFood ambrosia;
	public final ItemForestryFood honeyPot;
	public final ItemInfuser infuser;

	public ItemRegistryFood() {
		// / FOOD ITEMS
		honeyedSlice = registerItem(new ItemForestryFood(8, 0.6f), "honeyed_slice");
		beverage = registerItem(new ItemBeverage(), "beverage");
		ambrosia = registerItem(new ItemAmbrosia().setIsDrink(), "ambrosia");
		honeyPot = registerItem(new ItemForestryFood(2, 0.2f).setIsDrink(), "honey_pot");

		// / SEASONER
		infuser = new ItemInfuser();
		registerItem(infuser, "infuser");

		// Mead
		BeverageEffect.saveEffects(new ItemStack(beverage), Collections.singletonList(BeverageEffects.weakAlcoholic));
	}
}
