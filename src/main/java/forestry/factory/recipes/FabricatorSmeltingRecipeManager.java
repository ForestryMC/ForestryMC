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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingManager;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.utils.ItemStackUtil;

public class FabricatorSmeltingRecipeManager implements IFabricatorSmeltingManager {
	public static final Set<IFabricatorSmeltingRecipe> recipes = new HashSet<>();
	private static final Set<Fluid> recipeFluids = new HashSet<>();

	@Nullable
	public static IFabricatorSmeltingRecipe findMatchingSmelting(ItemStack resource) {
		if (resource.isEmpty()) {
			return null;
		}

		for (IFabricatorSmeltingRecipe smelting : recipes) {
			if (ItemStackUtil.isCraftingEquivalent(smelting.getResource(), resource)) {
				return smelting;
			}
		}

		return null;
	}

	@Override
	public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
		addRecipe(new FabricatorSmeltingRecipe(resource, molten, meltingPoint));
	}

	@Override
	public boolean addRecipe(IFabricatorSmeltingRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(IFabricatorSmeltingRecipe recipe) {
		return recipes.remove(recipe);
	}

	@Override
	public Collection<IFabricatorSmeltingRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	public static Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (IFabricatorSmeltingRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getProduct();
				if (fluidStack != null) {
					recipeFluids.add(fluidStack.getFluid());
				}
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}
}
