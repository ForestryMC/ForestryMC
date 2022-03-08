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

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class AbstractCraftingProvider<T extends IForestryRecipe> implements ICraftingProvider<T> {

	private final RecipeType<T> type;
	private final List<T> globalRecipes = new ArrayList<>();

	public AbstractCraftingProvider(RecipeType<T> type) {
		this.type = type;
	}

	@Override
	public boolean addRecipe(T recipe) {
		return globalRecipes.add(recipe);
	}

	@Override
	public List<T> getRecipes(@Nullable RecipeManager recipeManager) {
		recipeManager = adjust(recipeManager);
		Collection<T> values = recipeManager.getAllRecipesFor(type);
		return Stream.concat(globalRecipes.stream(), values.stream())
				.distinct()
				.toList();
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
