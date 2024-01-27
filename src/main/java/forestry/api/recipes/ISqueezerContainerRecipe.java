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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraftforge.registries.ObjectHolder;

public interface ISqueezerContainerRecipe extends IForestryRecipe, ISqueezerRecipe {

	RecipeType<ISqueezerContainerRecipe> TYPE = RecipeManagers.create("forestry:squeezer_container");

	class Companion {
		@ObjectHolder(registryName = "recipe_serializer", value = "forestry:squeezer_container")
		public static final RecipeSerializer<ISqueezerContainerRecipe> SERIALIZER = null;
	}

	ItemStack getEmptyContainer();

	int getProcessingTime();

	ItemStack getRemnants();

	float getRemnantsChance();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
