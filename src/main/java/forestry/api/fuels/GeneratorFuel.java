/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraftforge.fluids.FluidStack;

public class GeneratorFuel {

	/**
	 * LiquidStack representing the fuel type and amount consumed per triggered cycle.
	 */
	public final FluidStack fuelConsumed;
	/**
	 * EU emitted per tick while this fuel is being consumed in the generator (i.e. biofuel = 32, biomass = 8).
	 */
	public final int eu;
	/**
	 * Rate at which the fuel is consumed. 1 - Every tick 2 - Every second tick 3 - Every third tick etc.
	 */
	public final int rate;

	public GeneratorFuel(FluidStack fuelConsumed, int eu, int rate) {
		this.fuelConsumed = fuelConsumed;
		this.eu = eu;
		this.rate = rate;
	}

}
