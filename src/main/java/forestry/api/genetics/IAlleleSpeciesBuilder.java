/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public interface IAlleleSpeciesBuilder<C extends IChromosomeType<C>> {

	IAlleleSpecies<C> build();

	IAlleleSpeciesBuilder<C> setTemperature(EnumTemperature temperature);

	IAlleleSpeciesBuilder<C> setHumidity(EnumHumidity humidity);

	IAlleleSpeciesBuilder<C> setHasEffect();

	/** Secret species are not shown in creative mode. */
	IAlleleSpeciesBuilder<C> setIsSecret();

	/** Uncounted species do not count toward total species discovered. */
	IAlleleSpeciesBuilder<C> setIsNotCounted();
}
