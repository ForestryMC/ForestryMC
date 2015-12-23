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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ISolderManager;

public class SolderManager implements ISolderManager {
	private static final List<CircuitRecipe> recipes = new ArrayList<>();

	@Override
	public void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
		if (layout == null) {
			throw new IllegalArgumentException("layout may not be null");
		}
		if (resource == null) {
			throw new IllegalArgumentException("resource may not be null");
		}
		if (circuit == null) {
			throw new IllegalArgumentException("circuit may not be null");
		}
		recipes.add(new CircuitRecipe(layout, resource, circuit));
	}

	public static ICircuit getCircuit(ICircuitLayout layout, ItemStack resource) {
		CircuitRecipe circuitRecipe = getMatchingRecipe(layout, resource);
		if (circuitRecipe == null) {
			return null;
		}
		return circuitRecipe.getCircuit();
	}

	public static CircuitRecipe getMatchingRecipe(ICircuitLayout layout, ItemStack resource) {
		if (layout == null || resource == null) {
			return null;
		}

		for (CircuitRecipe recipe : recipes) {
			if (recipe.matches(layout, resource)) {
				return recipe;
			}
		}

		return null;
	}
}
