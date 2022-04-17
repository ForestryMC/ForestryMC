/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

public interface IStillRecipe extends IForestryRecipe {
	/**
	 * @return Amount of work cycles required to run through the conversion once.
	 */
	int getCyclesPerUnit();

	/**
	 * @return FluidStack representing the input liquid.
	 */
	FluidStack getInput();

	/**
	 * @return FluidStack representing the output liquid.
	 */
	FluidStack getOutput();
}
