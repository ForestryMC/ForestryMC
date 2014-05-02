/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.world.World;

import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IEffectData;

/**
 * Simple allele encapsulating a leaf effect. (Not implemented)
 */
public interface IAlleleLeafEffect extends IAlleleEffect {

	IEffectData doEffect(ITreeGenome genome, IEffectData storedData, World world, int x, int y, int z);

}
