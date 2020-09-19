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

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingRecipe;

public class FabricatorSmeltingRecipe implements IFabricatorSmeltingRecipe {

	private final Ingredient resource;
	private final FluidStack product;
	private final int meltingPoint;

	public FabricatorSmeltingRecipe(Ingredient resource, FluidStack molten, int meltingPoint) {
		Preconditions.checkNotNull(resource);
		Preconditions.checkArgument(!resource.hasNoMatchingItems());
		Preconditions.checkNotNull(molten);

		this.resource = resource;
		this.product = molten;
		this.meltingPoint = meltingPoint;
	}

	@Override
	public Ingredient getResource() {
		return resource;
	}

	@Override
	public FluidStack getProduct() {
		return product;
	}

	@Override
	public int getMeltingPoint() {
		return meltingPoint;
	}
}
