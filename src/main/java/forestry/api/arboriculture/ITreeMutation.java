/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.world.World;

import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;

public interface ITreeMutation extends IMutation {
	
	/**
	 * @return {@link ISpeciesRoot} this mutation is associated with.
	 */
	ITreeRoot getRoot();

	/**
	 * @return float representing the percent chance for mutation to occur, from 0.0 to 100.0.
	 * @since Forestry 4.0
	 */
	float getChance(World world, int x, int y, int z, IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, ITreeGenome genome0, ITreeGenome genome1);
}
