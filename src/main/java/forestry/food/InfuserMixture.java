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

import net.minecraft.item.ItemStack;

import forestry.api.food.IBeverageEffect;

/**
 * describes the itemstacks required to achieve a certain effect.
 */
public class InfuserMixture {
	private final int meta;
	private final ItemStack[] ingredients;
	private final IBeverageEffect effect;

	public InfuserMixture(int meta, ItemStack ingredient, IBeverageEffect effect) {
		this(meta, new ItemStack[]{ingredient}, effect);
	}

	public InfuserMixture(int meta, ItemStack ingredients[], IBeverageEffect effect) {
		this.meta = meta;
		this.ingredients = ingredients;
		this.effect = effect;
	}

	public ItemStack[] getIngredients() {
		return ingredients;
	}

	public boolean isIngredient(ItemStack itemstack) {
		for (ItemStack ingredient : ingredients) {
			if (ingredient.getItemDamage() < 0 && ingredient.getItem() == itemstack.getItem()) {
				return true;
			} else if (ingredient.getItemDamage() >= 0 && ingredient.isItemEqual(itemstack)) {
				return true;
			}
		}

		return false;
	}

	public boolean matches(ItemStack[] res) {

		// No recipe without resource!
		if (res == null || res.length <= 0) {
			return false;
		}

		boolean matchedAll = true;

		for (ItemStack stack : ingredients) {
			boolean matched = false;
			for (ItemStack matchStack : res) {
				if (matchStack == null) {
					continue;
				}

				// Check item matching
				if (stack.getItemDamage() < 0 && stack.getItem() == matchStack.getItem()) {
					if (stack.stackSize <= matchStack.stackSize) {
						matched = true;
						break;
					}
				} else if (stack.getItemDamage() >= 0 && stack.isItemEqual(matchStack)) {
					if (stack.stackSize <= matchStack.stackSize) {
						matched = true;
						break;
					}
				}
			}
			if (!matched) {
				matchedAll = false;
			}
		}
		return matchedAll;

	}

	public IBeverageEffect getEffect() {
		return this.effect;
	}

	public int getMeta() {
		return meta;
	}

	public int getWeight() {
		return ingredients.length;
	}
}
