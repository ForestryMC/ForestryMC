/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.INBTTagable;

/**
 * Implementations other than Forestry's default one are not supported!
 * 
 * @author SirSengir
 */
public interface IChromosome extends INBTTagable {

	IAllele getPrimaryAllele();

	IAllele getSecondaryAllele();

	IAllele getInactiveAllele();

	IAllele getActiveAllele();

}
