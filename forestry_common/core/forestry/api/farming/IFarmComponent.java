/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.farming;

import forestry.api.core.ITileStructure;

public interface IFarmComponent extends ITileStructure {

	boolean hasFunction();

	void registerListener(IFarmListener listener);

	void removeListener(IFarmListener listener);
}
