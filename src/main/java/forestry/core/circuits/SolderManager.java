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
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISolderManager;
import forestry.api.recipes.ISolderRecipe;

public class SolderManager implements ISolderManager {
	private static final List<ISolderRecipe> recipes = new ArrayList<>();

	@Override
	public void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
		Preconditions.checkNotNull(layout, "layout may not be null");
		Preconditions.checkNotNull(resource, "resource may not be null");
		Preconditions.checkNotNull(circuit, "circuit may not be null");

		recipes.add(new CircuitRecipe(IForestryRecipe.anonymous(), layout, resource, circuit));
	}

	@Nullable
	public static ICircuit getCircuit(ICircuitLayout layout, ItemStack resource) {
		ISolderRecipe circuitRecipe = getMatchingRecipe(layout, resource);
		if (circuitRecipe == null) {
			return null;
		}
		return circuitRecipe.getCircuit();
	}

	@Nullable
	public static ISolderRecipe getMatchingRecipe(@Nullable ICircuitLayout layout, ItemStack resource) {
		if (layout != null) {
			for (ISolderRecipe recipe : recipes) {
				if (recipe.matches(layout, resource)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Override
	public boolean addRecipe(ISolderRecipe recipe) {
		return recipes.add(recipe);
	}
}
