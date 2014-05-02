/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming;

import forestry.api.core.IStructureLogic;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmInterface;
import forestry.farming.gadgets.StructureLogicFarm;

public class FarmHelper implements IFarmInterface {

	@Override
	public IStructureLogic createFarmStructureLogic(IFarmComponent structure) {
		return new StructureLogicFarm(structure);
	}

}
