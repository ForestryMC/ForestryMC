/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

public interface IHygroregulatorRecipe extends IForestryRecipe {

	/**
	 * @return FluidStack containing information on fluid and amount.
	 */
	FluidStack getResource();

	/**
	 * @return The time between the removal of the fluid from the tank and the actual addition to the alveary climate.
	 */
	int getTransferTime();

	/**
	 * @return The humidity change that this recipe causes in one work cycle.
	 */
	float getHumidChange();

	/**
	 * @return The temperature change that this recipe causes in one work cycle.
	 */
	float getTempChange();
}
