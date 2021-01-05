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
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

import net.minecraftforge.registries.ObjectHolder;

public interface ISqueezerContainerRecipe extends IForestryRecipe, ISqueezerRecipe {

	IRecipeType<ISqueezerContainerRecipe> TYPE = RecipeManagers.create("forestry:moistener");

	class Companion {
		@ObjectHolder("forestry:moistener")
		public static final IRecipeSerializer<ISqueezerContainerRecipe> SERIALIZER = null;
	}

	ItemStack getEmptyContainer();

	int getProcessingTime();

	ItemStack getRemnants();

	float getRemnantsChance();

	@Override
	default IRecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default IRecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
