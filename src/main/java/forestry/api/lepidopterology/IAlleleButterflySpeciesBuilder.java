/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.genetics.IAlleleSpeciesBuilder;

public interface IAlleleButterflySpeciesBuilder extends IAlleleSpeciesBuilder {

	@Override
	IAlleleButterflySpecies build();

	IAlleleButterflySpeciesBuilder setRarity(float rarity);

	IAlleleButterflySpeciesBuilder setFlightDistance(float flightDistance);

	IAlleleButterflySpeciesBuilder setNocturnal();
}
