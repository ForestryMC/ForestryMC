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
import net.minecraft.util.NonNullList;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;

public class FabricatorRecipe implements IFabricatorRecipe {
	private final ItemStack plan;
	private final FluidStack molten;
	private final NonNullList<NonNullList<ItemStack>> ingredients;
	private final NonNullList<String> oreDicts;
	private final ItemStack result;
	private final int width;
	private final int height;

	public FabricatorRecipe(ItemStack plan, FluidStack molten, ItemStack result, NonNullList<NonNullList<ItemStack>> ingredients, NonNullList<String> oreDicts, int width, int height) {
		Preconditions.checkNotNull(plan);
		Preconditions.checkNotNull(molten);
		Preconditions.checkNotNull(result);
		Preconditions.checkArgument(!result.isEmpty());
		Preconditions.checkNotNull(ingredients);
		Preconditions.checkNotNull(oreDicts);
		Preconditions.checkArgument(width > 0);
		Preconditions.checkArgument(height > 0);
		this.plan = plan;
		this.molten = molten;
		this.result = result;
		this.ingredients = ingredients;
		this.oreDicts = oreDicts;
		this.width = width;
		this.height = height;
	}

	@Override
	public NonNullList<NonNullList<ItemStack>> getIngredients() {
		return ingredients;
	}

	@Override
	public NonNullList<String> getOreDicts() {
		return oreDicts;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public ItemStack getPlan() {
		return plan;
	}

	@Override
	public FluidStack getLiquid() {
		return molten;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}
}
