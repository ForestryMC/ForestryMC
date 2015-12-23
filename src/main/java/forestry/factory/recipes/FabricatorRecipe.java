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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.utils.ItemStackUtil;

public class FabricatorRecipe implements IFabricatorRecipe {

	private final ItemStack plan;
	private final FluidStack molten;
	private final ShapedRecipeCustom internal;

	public FabricatorRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] ingredients) {
		this(plan, molten, ShapedRecipeCustom.createShapedRecipe(result, ingredients));
	}

	public FabricatorRecipe(ItemStack plan, FluidStack molten, ShapedRecipeCustom internal) {
		this.plan = plan;
		this.molten = molten;
		this.internal = internal;
	}

	@Override
	@Deprecated
	public boolean matches(@Nullable ItemStack plan, ItemStack[][] resources) {
		if (this.plan != null && !ItemStackUtil.isCraftingEquivalent(this.plan, plan)) {
			return false;
		}

		return RecipeUtil.matches(internal.getIngredients(), internal.getWidth(), internal.getHeight(), resources);
	}

	@Override
	public Object[] getIngredients() {
		return internal.getIngredients();
	}

	@Override
	public int getWidth() {
		return internal.getWidth();
	}

	@Override
	public int getHeight() {
		return internal.getHeight();
	}

	@Override
	public boolean preservesNbt() {
		return false;
	}

	@Override
	@Nullable
	public ItemStack getPlan() {
		return plan;
	}

	@Override
	public FluidStack getLiquid() {
		return molten;
	}

	@Override
	public ItemStack getCraftingResult(IInventory craftingInventory) {
		return internal.getRecipeOutput().copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return internal.getRecipeOutput();
	}
}
