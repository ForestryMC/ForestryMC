/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import forestry.api.farming.IFarmListener;

/**
 * Needs to be implemented by TileEntities that want to be part of a farm.
 * The sub-interfaces can be implemented to alter the operation of the farm.
 * They are automatically detected and handled by the farm when they join its structure.
 */
public interface IFarmComponent<T extends IMultiblockLogicFarm> extends IMultiblockComponent {

	/**
	 * @return the multiblock logic for this component
	 */
	T getMultiblockLogic();

	/**
	 * Implemented by farm parts to apply a farmListener to the completed structure.
	 */
	interface Listener extends IFarmComponent {
		IFarmListener getFarmListener();
	}

	/**
	 * Implemented by farm parts to receive ticks from the completed structure.
	 */
	interface Active extends IFarmComponent {
		void updateServer(int tickCount);
		void updateClient(int tickCount);
	}
}
