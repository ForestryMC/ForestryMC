/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

public interface IMultiblockLogicFarm extends IMultiblockLogic {
	/**
	 * @return the multiblock controller for this logic
	 */
	@Override
	IFarmController getController();
}
