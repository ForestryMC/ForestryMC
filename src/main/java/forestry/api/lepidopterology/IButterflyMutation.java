/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import net.minecraft.world.World;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;

public interface IButterflyMutation extends IMutation {
	float getChance(World world, IButterflyNursery housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1);
}
