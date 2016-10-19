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

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BottlerRecipe {
	
	public static BottlerRecipe createFilled(ItemStack filled) {
		if (filled == null) {
			return null;
		}

		ItemStack empty = filled.copy();
		empty.stackSize = 1;
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return null;
		}

		FluidStack fill = fluidHandler.drain(Integer.MAX_VALUE, true);
		if (fill != null && fill.amount > 0) {
			return new BottlerRecipe(empty, fill, filled, false);
		}

		return null;
	}
	
	public static BottlerRecipe createEmpty(Fluid res, ItemStack empty) {
		if (res == null || empty == null) {
			return null;
		}

		ItemStack filled = empty.copy();
		filled.stackSize = 1;

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(filled);
		if (fluidHandler == null) {
			return null;
		}

		int fillAmount = fluidHandler.fill(new FluidStack(res, Integer.MAX_VALUE), true);
		if (fillAmount > 0) {
			return new BottlerRecipe(empty, new FluidStack(res, fillAmount), filled, true);
		}

		return null;
	}

	public final FluidStack input;
	public final ItemStack empty;
	public final ItemStack filled;
	public final boolean fillRecipe;

	private BottlerRecipe(ItemStack empty, FluidStack input, ItemStack filled, boolean fillRecipe) {
		this.input = input;
		this.empty = empty;
		this.filled = filled;
		this.fillRecipe = fillRecipe;
	}

	public boolean matcheEmpty(ItemStack emptyCan, FluidStack resource) {
		return emptyCan != null && resource != null && emptyCan.isItemEqual(empty) && resource.containsFluid(input) && fillRecipe;
	}
	
	public boolean matcheFilled(ItemStack filledCan) {
		return filled != null && !fillRecipe && filled.isItemEqual(filledCan);
	}
}
