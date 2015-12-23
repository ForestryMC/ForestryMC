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
package forestry.food;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.food.IIngredientManager;

public class InfuserIngredientManager implements IIngredientManager {
	private final List<InfuserIngredient> ingredients = new ArrayList<>();

	@Override
	public void addIngredient(ItemStack ingredient, String description) {
		this.ingredients.add(new InfuserIngredient(ingredient, description));
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		if (itemstack == null) {
			return null;
		}

		for (InfuserIngredient ingredient : ingredients) {
			if (ingredient.getIngredient().getItemDamage() < 0 && ingredient.getIngredient().getItem() == itemstack.getItem()) {
				return ingredient.getDescription();
			} else if (ingredient.getIngredient().getItemDamage() >= 0 && ingredient.getIngredient().isItemEqual(itemstack)) {
				return ingredient.getDescription();
			}
		}

		return null;
	}
}
