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
import net.minecraftforge.fluids.IFluidContainerItem;

import forestry.core.config.Constants;
import forestry.core.fluids.FluidHelper;

public class BottlerRecipe {
	public final FluidStack input;
	public final ItemStack empty;
	public final ItemStack filled;

	private BottlerRecipe(ItemStack empty, FluidStack input, ItemStack filled) {
		this.input = input;
		if (empty.getItem() instanceof IFluidContainerItem) {
			FluidStack emptyFluid = FluidHelper.getFluidStackInContainer(empty);
			if (emptyFluid != null) {
				this.input.amount -= emptyFluid.amount;
			}
			if (this.input.amount > Constants.BUCKET_VOLUME) {
				this.input.amount = Constants.BUCKET_VOLUME;
			}
		}
		this.empty = empty;
		this.filled = filled;
	}

	public boolean matches(ItemStack emptyCan, FluidStack resource) {
		if (emptyCan == null || resource == null || !emptyCan.isItemEqual(empty)) {
			return false;
		}

		if (empty.getItem() instanceof IFluidContainerItem) {
			return true;
		} else {
			return resource.containsFluid(input);
		}
	}

	public static BottlerRecipe getRecipe(FluidStack res, ItemStack empty) {
		if (res == null || empty == null) {
			return null;
		}

		ItemStack filled = FluidHelper.getFilledContainer(res.getFluid(), empty);
		if (filled == null) {
			return null;
		}

		FluidStack input = FluidHelper.getFluidStackInContainer(filled);
		if (input == null) {
			return null;
		}

		return new BottlerRecipe(empty, input, filled);
	}
}
