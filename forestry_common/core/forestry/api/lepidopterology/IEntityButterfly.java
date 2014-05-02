/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.lepidopterology;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;

import forestry.api.genetics.IIndividual;

public interface IEntityButterfly extends IAnimals {

	void changeExhaustion(int change);

	int getExhaustion();

	IButterfly getButterfly();

	/**
	 * @return The entity as an EntityCreature to save casting.
	 */
	EntityCreature getEntity();

	IIndividual getPollen();

	void setPollen(IIndividual pollen);
}
