/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import genetics.api.individual.IGenome;

import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IEffectData;

/**
 * Simple allele encapsulating a leaf effect. (Not implemented)
 */
public interface IAlleleLeafEffect extends IAlleleEffect {

	IEffectData doEffect(IGenome genome, IEffectData storedData, World world, BlockPos pos);

}
