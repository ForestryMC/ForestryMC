/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IEffectData;

public interface IAlleleButterflyEffect extends IAlleleEffect {

	/**
	 * Used by butterflies to trigger effects in the world.
	 * @param butterfly {@link IEntityButterfly}
	 * @param storedData
	 * @return {@link forestry.api.genetics.IEffectData} for the next cycle.
	 */
	IEffectData doEffect(IEntityButterfly butterfly, IEffectData storedData);

}
