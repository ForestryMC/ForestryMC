/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public interface IAlleleSpeciesBuilder {

	IAlleleSpecies build();

	IAlleleSpeciesBuilder setTemperature(EnumTemperature temperature);

	IAlleleSpeciesBuilder setHumidity(EnumHumidity humidity);

	IAlleleSpeciesBuilder setHasEffect();

	/**
	 * Secret species are not shown in creative mode.
	 */
	IAlleleSpeciesBuilder setIsSecret();

	/**
	 * Uncounted species do not count toward total species discovered.
	 */
	IAlleleSpeciesBuilder setIsNotCounted();

	/**
	 * Manually the genetic complexity.
	 * If this is not set, the complexity is based on the number of breeding steps to reach this species.
	 *
	 * @see forestry.api.genetics.IAlleleSpecies#getComplexity()
	 */
	void setComplexity(int complexity);
}
