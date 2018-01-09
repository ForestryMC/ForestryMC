/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IGenome;

public interface ITreeGenome extends IGenome {

	@Override
	IAlleleTreeSpecies getPrimary();

	@Override
	IAlleleTreeSpecies getSecondary();

	IFruitProvider getFruitProvider();

	float getHeight();

	float getFertility();

	/**
	 * @return Determines either a) how many fruit leaves there are or b) the chance for any fruit leave to drop a sapling. Exact usage determined by the
	 * IFruitProvider
	 */
	float getYield();

	float getSappiness();

	/**
	 * @return Amount of random block ticks required for a sapling to mature into a fully grown tree.
	 */
	int getMaturationTime();

	int getGirth();

	boolean getFireproof();

	IAlleleLeafEffect getEffect();
	
	/**
	 * 
	 * @return A ItemStack that is used in getPickBlock of the leave block.
	 */
	ItemStack getDecorativeLeaves();

	/**
	 * @return true if this genome matches one of the default template genomes
	 */
	boolean matchesTemplateGenome();
}
