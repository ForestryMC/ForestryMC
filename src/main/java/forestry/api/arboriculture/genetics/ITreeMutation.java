/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import genetics.api.individual.IGenome;
import genetics.api.mutation.IMutation;

import forestry.api.genetics.IForestrySpeciesRoot;

public interface ITreeMutation extends IMutation {

	/**
	 * @return {@link IForestrySpeciesRoot} this mutation is associated with.
	 */
	@Override
	ITreeRoot getRoot();

	/**
	 * @return float representing the percent chance for mutation to occur, from 0.0 to 100.0.
	 * @since Forestry 4.0
	 */
	float getChance(Level world, BlockPos pos, IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, IGenome genome0, IGenome genome1);
}
