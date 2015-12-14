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

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.fluids.FluidHelper;
import forestry.core.utils.ItemStackUtil;

public class SqueezerContainerRecipe implements ISqueezerContainerRecipe {

	private final ItemStack emptyContainer;
	private final int processingTime;
	private final ItemStack remnants;
	private final float remnantsChance;

	public SqueezerContainerRecipe(ItemStack emptyContainer, int processingTime, ItemStack remnants, float remnantsChance) {
		this.emptyContainer = emptyContainer;
		this.processingTime = processingTime;
		this.remnants = remnants;
		this.remnantsChance = remnantsChance;
	}

	@Override
	public ItemStack getEmptyContainer() {
		return emptyContainer;
	}

	@Override
	public int getProcessingTime() {
		return processingTime;
	}

	@Override
	public ItemStack getRemnants() {
		return remnants;
	}

	@Override
	public float getRemnantsChance() {
		return remnantsChance;
	}

	@Override
	public ISqueezerRecipe getSqueezerRecipe(ItemStack filledContainer) {
		if (filledContainer == null) {
			return null;
		}
		FluidStack fluidOutput = FluidHelper.getFluidStackInContainer(filledContainer);
		if (fluidOutput == null) {
			return null;
		}
		ItemStack filledContainerCopy = ItemStackUtil.createSplitStack(filledContainer, 1);
		return new SqueezerRecipe(processingTime, new ItemStack[]{filledContainerCopy}, fluidOutput, remnants, remnantsChance);
	}

}
