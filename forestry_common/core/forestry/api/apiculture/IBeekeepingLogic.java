/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.core.INBTTagable;
import forestry.api.genetics.IEffectData;

public interface IBeekeepingLogic extends INBTTagable {

	/* STATE INFORMATION */
	int getBreedingTime();

	int getTotalBreedingTime();

	IBee getQueen();

	IBeeHousing getHousing();
	
	IEffectData[] getEffectData();

	/* UPDATING */
	void update();

}
