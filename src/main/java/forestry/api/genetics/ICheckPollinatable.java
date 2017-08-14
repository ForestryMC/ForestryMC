/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraftforge.common.EnumPlantType;

/**
 * Used to check for pollination traits without altering the world by changing vanilla leaves to forestry ones.
 * For a normal pollinatable that can be mated, see {@link IPollinatable}.
 */
public interface ICheckPollinatable {

	/**
	 * @return plant type this pollinatable is classified as.
	 * (Can be used by bees to determine whether to interact or not.)
	 */
	EnumPlantType getPlantType();

	/**
	 * @return IIndividual containing the genetic information of this IPollinatable
	 */
	IIndividual getPollen();

	/**
	 * Checks whether this can mate with the given pollen.
	 * <p>
	 * Must be the one to check genetic equivalency.
	 *
	 * @param pollen IIndividual representing the pollen.
	 * @return true if mating is possible, false otherwise.
	 */
	boolean canMateWith(IIndividual pollen);

	/**
	 * @return true if this has already been pollinated.
	 */
	boolean isPollinated();
}
