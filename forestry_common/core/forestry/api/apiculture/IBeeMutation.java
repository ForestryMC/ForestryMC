/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;

public interface IBeeMutation extends IMutation {
	
	IBeeRoot getRoot();
	
	/**
	 * @param housing
	 * @param allele0
	 * @param allele1
	 * @param genome0
	 * @param genome1
	 * @return float representing the chance for mutation to occur. note that this is 0 - 100 based, since it was an integer previously!
	 */
	float getChance(IBeeHousing housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1);
}
