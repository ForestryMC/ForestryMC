/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * Basic effect allele. 
 */
public interface IAlleleEffect extends IAllele {
	/**
	 * @return true if this effect can combine with the effect on other allele (i.e. run before or after). combination can only occur if both effects are
	 *         combinable.
	 */
	boolean isCombinable();

	/**
	 * Returns the passed data storage if it is valid for this effect or a new one if the passed storage object was invalid for this effect.
	 * 
	 * @param storedData
	 * @return {@link IEffectData} for the next cycle.
	 */
	IEffectData validateStorage(IEffectData storedData);

}
