/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * Can be implemented by tile entities, if they wish to be pollinatable.
 *
 * @author SirSengir
 */
public interface IPollinatable extends ICheckPollinatable {

	/**
	 * Pollinates this entity.
	 *
	 * @param pollen IIndividual representing the pollen.
	 */
	void mateWith(IIndividual pollen);
}
