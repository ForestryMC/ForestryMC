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
package forestry.factory.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import forestry.api.recipes.IMoistenerRecipe;

public class MoistenerRecipe implements IMoistenerRecipe {

	private final int timePerItem;
	private final Ingredient resource;
	private final ItemStack product;

	public MoistenerRecipe(Ingredient resource, ItemStack product, int timePerItem) {
		this.timePerItem = timePerItem;
		this.resource = resource;
		this.product = product;
	}

	@Override
	public int getTimePerItem() {
		return timePerItem;
	}

	@Override
	public Ingredient getResource() {
		return resource;
	}

	@Override
	public ItemStack getProduct() {
		return product;
	}
}
