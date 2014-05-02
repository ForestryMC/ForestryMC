/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.world.World;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;

public interface ITreeMutation extends IMutation {
	
	/**
	 * @return {@link ISpeciesRoot} this mutation is associated with.
	 */
	ITreeRoot getRoot();
	
	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param allele0
	 * @param allele1
	 * @param genome0
	 * @param genome1
	 * @return float representing the chance for mutation to occur. note that this is 0 - 100 based, since it was an integer previously!
	 */
	float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1);
}
