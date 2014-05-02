/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IGenome;

public interface ITreeGenome extends IGenome {

	IAlleleTreeSpecies getPrimary();

	IAlleleTreeSpecies getSecondary();

	IFruitProvider getFruitProvider();

	IGrowthProvider getGrowthProvider();

	float getHeight();

	float getFertility();

	/**
	 * @return Determines either a) how many fruit leaves there are or b) the chance for any fruit leave to drop a sapling. Exact usage determined by the
	 *         IFruitProvider
	 */
	float getYield();

	float getSappiness();

	EnumSet<EnumPlantType> getPlantTypes();

	/**
	 * @return Amount of random block ticks required for a sapling to mature into a fully grown tree.
	 */
	int getMaturationTime();

	int getGirth();

	IAlleleLeafEffect getEffect();
}
