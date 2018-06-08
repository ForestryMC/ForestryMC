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

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.utils.ItemStackUtil;

public class SqueezerContainerRecipe implements ISqueezerContainerRecipe {

	private final ItemStack emptyContainer;
	private final int processingTime;
	private final ItemStack remnants;
	private final float remnantsChance;

	public SqueezerContainerRecipe(ItemStack emptyContainer, int processingTime, ItemStack remnants, float remnantsChance) {
		Preconditions.checkNotNull(emptyContainer);
		Preconditions.checkArgument(!emptyContainer.isEmpty());
		Preconditions.checkNotNull(remnants);

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
	@Nullable
	public ISqueezerRecipe getSqueezerRecipe(ItemStack filledContainer) {
		if (filledContainer.isEmpty()) {
			return null;
		}
		FluidStack fluidOutput = FluidUtil.getFluidContained(filledContainer);
		if (fluidOutput == null) {
			return null;
		}
		ItemStack filledContainerCopy = ItemStackUtil.createCopyWithCount(filledContainer, 1);
		NonNullList<ItemStack> input = NonNullList.create();
		input.add(filledContainerCopy);
		return new SqueezerRecipe(processingTime, input, fluidOutput, remnants, remnantsChance);
	}

}
