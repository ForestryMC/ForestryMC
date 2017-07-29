/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import forestry.api.greenhouse.IGreenhouseListener;

public interface IGreenhouseComponent<T extends IMultiblockLogicGreenhouse> extends IMultiblockComponent {
	@Override
	T getMultiblockLogic();

	interface Listener extends IGreenhouseComponent {
		IGreenhouseListener getGreenhouseListener();
	}
	
	interface Active extends IGreenhouseComponent {
		void updateServer(int tickCount);

		void updateClient(int tickCount);
	}

}
