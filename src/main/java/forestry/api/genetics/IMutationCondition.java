/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.climate.IClimateProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMutationCondition {

	/**
	 * Returns a float from 0 to 1 representing the chance for mutation to occur.
	 * Most will return 1 if the condition is met and 0 otherwise,
	 * but the float offers flexibility for more advanced conditions.
	 */
	float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate);

	/**
	 * A localized description of the mutation condition. (i.e. "A temperature of HOT is required.")
	 */
	String getDescription();
}
