/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITreeMutation extends IMutation {

	/**
	 * @return {@link ISpeciesRoot} this mutation is associated with.
	 */
	@Override
	ITreeRoot getRoot();

	/**
	 * @return float representing the percent chance for mutation to occur, from 0.0 to 100.0.
	 * @since Forestry 4.0
	 */
	float getChance(World world, BlockPos pos, IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, ITreeGenome genome0, ITreeGenome genome1);
}
