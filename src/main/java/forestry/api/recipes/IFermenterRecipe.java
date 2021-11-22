/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IFermenterRecipe extends IForestryRecipe {
	/**
	 * @return ItemStack representing the input resource.
	 */
	ItemStack getResource();

	/**
	 * @return FluidStack representing the input fluid resource.
	 */
	FluidStack getFluidResource();

	/**
	 * @return Value of the given resource, i.e. how much needs to be fermented for the output to be deposited into the product tank.
	 */
	int getFermentationValue();

	/**
	 * @return Modifies the amount of liquid output per work cycle.
	 * (water = 1.0f, honey = 1.5f)
	 */
	float getModifier();

	/**
	 * @return Fluid representing output. Amount is determined by fermentationValue * modifier.
	 */
	Fluid getOutput();
}
