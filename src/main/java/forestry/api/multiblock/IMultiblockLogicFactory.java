/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

/**
 * Creates new instances of IMultiblockLogic.
 * Each IMultiblockComponent needs its own instance of IMultiblockLogic.
 */
public interface IMultiblockLogicFactory {
	IMultiblockLogicAlveary createAlvearyLogic();

	IMultiblockLogicFarm createFarmLogic();
}
