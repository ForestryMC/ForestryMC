/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.farming;

import forestry.api.core.IStructureLogic;

public interface IFarmInterface {

	/**
	 * Creates {@link IStructureLogic} for use in farm components.
	 * 
	 * @param structure
	 *            {@link IFarmComponent} to create the logic for.
	 * @return {@link IStructureLogic} for use in farm components
	 */
	IStructureLogic createFarmStructureLogic(IFarmComponent structure);
}
