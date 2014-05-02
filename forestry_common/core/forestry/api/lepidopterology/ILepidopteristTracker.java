/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.genetics.IBreedingTracker;

public interface ILepidopteristTracker extends IBreedingTracker {

	void registerCatch(IButterfly butterfly);
	
}
