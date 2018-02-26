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
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class BottlerRecipe {
	@Nullable
	public static BottlerRecipe createEmptyingRecipe(ItemStack filled) {
		ItemStack empty = filled.copy();
		empty.setCount(1);
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return null;
		}

		FluidStack drained = fluidHandler.drain(Integer.MAX_VALUE, true);
		if (drained != null && drained.amount > 0) {
			return new BottlerRecipe(fluidHandler.getContainer(), drained, filled, false);
		}

		return null;
	}

	@Nullable
	public static BottlerRecipe createFillingRecipe(Fluid res, ItemStack empty) {
		ItemStack filled = empty.copy();
		filled.setCount(1);

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(filled);
		if (fluidHandler == null) {
			return null;
		}

		int fillAmount = fluidHandler.fill(new FluidStack(res, Integer.MAX_VALUE), true);
		if (fillAmount > 0) {
			return new BottlerRecipe(empty, new FluidStack(res, fillAmount), fluidHandler.getContainer(), true);
		}

		return null;
	}

	public final FluidStack fluid;
	public final ItemStack inputStack;
	public final ItemStack outputStack;
	public final boolean fillRecipe;

	public BottlerRecipe(ItemStack inputStack, FluidStack fluid, ItemStack outputStack, boolean fillRecipe) {
		this.fluid = fluid;
		this.inputStack = inputStack;
		this.outputStack = outputStack;
		this.fillRecipe = fillRecipe;
	}

	public boolean matchEmpty(ItemStack emptyCan, FluidStack resource) {
		return !emptyCan.isEmpty() && emptyCan.isItemEqual(inputStack) && resource.isFluidEqual(fluid) && fillRecipe;
	}

	public boolean matchFilled(ItemStack filledCan) {
		return !outputStack.isEmpty() && !fillRecipe && outputStack.isItemEqual(filledCan);
	}
}
