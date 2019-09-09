/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

import forestry.api.climate.IClimateProvider;

public interface IMutationCondition {

	/**
	 * Returns a float from 0 to 1 representing the chance for mutation to occur.
	 * Most will return 1 if the condition is met and 0 otherwise,
	 * but the float offers flexibility for more advanced conditions.
	 */
	float getChance(World world, BlockPos pos, IAllele firstParent, IAllele secondParent, IGenome firstGenome, IGenome secondGenome, IClimateProvider climate);

	/**
	 * A localized description of the mutation condition. (i.e. "A temperature of HOT is required.")
	 */
	String getDescription();
}
