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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fml.DistExecutor;

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;

public class AbstractCraftingProvider<T extends IForestryRecipe> implements ICraftingProvider<T> {

	private final IRecipeType<T> type;
	private final Set<T> globalRecipes = new HashSet<>();

	public AbstractCraftingProvider(IRecipeType<T> type) {
		this.type = type;
	}

	@Override
	public boolean addRecipe(T recipe) {
		return globalRecipes.add(recipe);
	}

	@Override
	public Collection<T> getRecipes(@Nullable RecipeManager recipeManager) {
		Set<T> recipes = new HashSet<>(globalRecipes);

		for (IRecipe<IInventory> recipe : adjust(recipeManager).byType(type).values()) {
			//noinspection unchecked
			recipes.add((T) recipe);
		}

		return recipes;
	}

	/**
	 * Allow RecipeManager to be null on the client
	 *
	 * @param recipeManager The given recipe manager
	 * @return A recipe manager which will not be null
	 */
	protected static RecipeManager adjust(@Nullable RecipeManager recipeManager) {
		if (recipeManager == null) {
			return DistExecutor.safeRunForDist(() -> ClientCraftingHelper::adjustClient, () -> ServerCraftingHelper::adjustServer);
		} else {
			return recipeManager;
		}
	}

}
