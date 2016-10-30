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

	public final FluidStack fluid;
	public final ItemStack inputStack;
	@Nullable
	public final ItemStack outputStack;
	public final boolean fillRecipe;

	public BottlerRecipe(ItemStack inputStack, FluidStack fluid, @Nullable ItemStack outputStack, boolean fillRecipe) {
		this.fluid = fluid;
		this.inputStack = inputStack;
		this.outputStack = outputStack;
		this.fillRecipe = fillRecipe;
	}

	public boolean matchEmpty(ItemStack emptyCan, FluidStack resource) {
		return emptyCan != null && resource != null && emptyCan.isItemEqual(inputStack) && resource.containsFluid(fluid) && fillRecipe;
	}

	public boolean matchFilled(ItemStack filledCan) {
		return outputStack != null && !fillRecipe && outputStack.isItemEqual(filledCan);
	}
}
