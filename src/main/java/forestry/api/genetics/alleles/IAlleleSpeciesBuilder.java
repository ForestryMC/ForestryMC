/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics.alleles;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public interface IAlleleSpeciesBuilder {

	IAlleleForestrySpecies build();

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
	 * @see IAlleleForestrySpecies#getComplexity()
	 */
	void setComplexity(int complexity);
}
