/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nonnull;

public class Farmables {
	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 *
	 * Identifiers: farmArboreal farmWheat farmGourd farmInfernal farmPoales farmSucculentes farmVegetables farmShroom
	 */
	@Nonnull
	public static final Multimap<String, IFarmable> farmables = HashMultimap.create();

}
