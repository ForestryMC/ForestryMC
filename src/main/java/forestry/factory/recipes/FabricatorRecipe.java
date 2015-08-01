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
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;

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
	public boolean matches(@Nullable ItemStack plan, ItemStack[][] resources) {
		if (this.plan != null && !StackUtils.isCraftingEquivalent(this.plan, plan)) {
			return false;
		}

		return internal.matches(resources);
	}

	@Override
	public Object[] getIngredients() {
		return internal.getIngredients();
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
		return internal.getCraftingResult(craftingInventory);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return internal.getRecipeOutput();
	}

	public FabricatorRecipe setPreserveNBT() {
		internal.setPreserveNBT();
		return this;
	}

}
