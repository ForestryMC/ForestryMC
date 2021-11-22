/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import forestry.api.farming.IFarmHousing;

/**
 * The IFarmController provides access to all the IMultiblockController and IFarmHousing methods
 * necessary for an IFarmComponent to function.
 */
public interface IFarmController extends IMultiblockController, IFarmHousing {

}
