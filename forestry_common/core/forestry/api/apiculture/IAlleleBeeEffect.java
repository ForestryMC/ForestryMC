/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IEffectData;

public interface IAlleleBeeEffect extends IAlleleEffect {
	
	/**
	 * Called by apiaries to cause an effect in the world.
	 * 
	 * @param genome
	 *            Genome of the bee queen causing this effect
	 * @param storedData
	 *            Object containing the stored effect data for the apiary/hive the bee is in.
	 * @param housing {@link IBeeHousing} the bee currently resides in.
	 * @return storedData, may have been manipulated.
	 */
	IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

	/**
	 * Is called to produce bee effects.
	 * 
	 * @param genome
	 * @param storedData
	 *            Object containing the stored effect data for the apiary/hive the bee is in.
	 * @param housing {@link IBeeHousing} the bee currently resides in.
	 * @return storedData, may have been manipulated.
	 */
	IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);
	
}
