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
package forestry.core.circuits;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISolderManager;
import forestry.api.recipes.ISolderRecipe;
import forestry.factory.recipes.AbstractCraftingProvider;

import java.util.Optional;

public class SolderManager extends AbstractCraftingProvider<ISolderRecipe> implements ISolderManager {

	public SolderManager() {
		super(ISolderRecipe.TYPE);
	}

	@Override
	public void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
		Preconditions.checkNotNull(layout, "layout may not be null");
		Preconditions.checkNotNull(resource, "resource may not be null");
		Preconditions.checkNotNull(circuit, "circuit may not be null");

		addRecipe(new CircuitRecipe(IForestryRecipe.anonymous(), layout, resource, circuit));
	}

	@Override
	public Optional<ICircuit> getCircuit(@Nullable RecipeManager recipeManager, ICircuitLayout layout, ItemStack resource) {
		return getMatchingRecipe(recipeManager, layout, resource)
				.map(ISolderRecipe::getCircuit);
	}

	@Override
	public Optional<ISolderRecipe> getMatchingRecipe(@Nullable RecipeManager recipeManager, @Nullable ICircuitLayout layout, ItemStack resource) {
		if (layout == null) {
			return Optional.empty();
		}
		return getRecipes(recipeManager)
				.filter(recipe -> recipe.matches(layout, resource))
				.findFirst();
	}
}
