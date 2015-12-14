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

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.ShapedRecipeCustom;

public class CarpenterRecipe implements ICarpenterRecipe {

	private final int packagingTime;
	@Nullable
	private final FluidStack liquid;
	@Nullable
	private final ItemStack box;
	private final ShapedRecipeCustom internal;

	public CarpenterRecipe(int packagingTime, @Nullable FluidStack liquid, @Nullable ItemStack box, ShapedRecipeCustom internal) {
		this.packagingTime = packagingTime;
		this.liquid = liquid;
		this.box = box;
		this.internal = internal;
	}

	public int getPackagingTime() {
		return packagingTime;
	}

	@Override
	@Nullable
	public ItemStack getBox() {
		return box;
	}

	@Override
	@Nullable
	public FluidStack getFluidResource() {
		return liquid;
	}

	@Override
	public IDescriptiveRecipe getCraftingGridRecipe() {
		return internal;
	}
}
