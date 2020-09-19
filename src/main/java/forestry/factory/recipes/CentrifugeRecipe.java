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

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import forestry.api.recipes.ICentrifugeRecipe;

public class CentrifugeRecipe implements ICentrifugeRecipe {

	private final int processingTime;
	private final Ingredient input;
	private final NonNullList<Product> outputs;

	public CentrifugeRecipe(int processingTime, Ingredient input, NonNullList<Product> outputs) {
		this.processingTime = processingTime;
		this.input = input;
		this.outputs = outputs;
	}

	@Override
	public Ingredient getInput() {
		return input;
	}

	@Override
	public int getProcessingTime() {
		return processingTime;
	}

	@Override
	public NonNullList<ItemStack> getProducts(Random random) {
		NonNullList<ItemStack> products = NonNullList.create();

		for (Product entry : this.outputs) {
			float probability = entry.getProbability();

			if (probability >= 1.0) {
				products.add(entry.getStack().copy());
			} else if (random.nextFloat() < probability) {
				products.add(entry.getStack().copy());
			}
		}

		return products;
	}

	@Override
	public NonNullList<Product> getAllProducts() {
		return outputs;
	}
}
