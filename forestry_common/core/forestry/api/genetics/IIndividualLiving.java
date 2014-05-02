/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.world.World;

public interface IIndividualLiving extends IIndividual {

	/**
	 * @return Genetic information of the mate, null if unmated.
	 */
	IGenome getMate();

	/**
	 * @return Current health of the individual.
	 */
	int getHealth();

	/**
	 * @return Maximum health of the individual.
	 */
	int getMaxHealth();

	/**
	 * Age the individual.
	 * @param world
	 * @param ageModifier
	 */
	void age(World world, float ageModifier);

	/**
	 * Mate with the given individual.
	 * @param individual the {@link IIndividual} to mate this one with.
	 */
	void mate(IIndividual individual);

	/**
	 * @return true if the individual is among the living.
	 */
	boolean isAlive();

}
