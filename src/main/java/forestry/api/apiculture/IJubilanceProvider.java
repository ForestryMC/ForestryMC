/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.genetics.IAlleleBeeSpecies;

public interface IJubilanceProvider {

	/**
	 * Returns true when conditions are right to make this species Jubilant.
	 * Jubilant bees can produce their Specialty products.
	 */
	boolean isJubilant(IAlleleBeeSpecies species, IGenome genome, IBeeHousing housing);
}
