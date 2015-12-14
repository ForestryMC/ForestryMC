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

import forestry.api.recipes.ISqueezerRecipe;

public interface ISqueezerContainerRecipe {
	ItemStack getEmptyContainer();

	int getProcessingTime();

	ItemStack getRemnants();

	float getRemnantsChance();

	ISqueezerRecipe getSqueezerRecipe(ItemStack filledContainer);
}
