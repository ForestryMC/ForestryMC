/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import forestry.api.greenhouse.IGreenhouseListener;
import forestry.api.lepidopterology.IButterflyCocoon;
import net.minecraft.item.ItemStack;

public interface IGreenhouseComponent<T extends IMultiblockLogicGreenhouse> extends IMultiblockComponent {
	@Override
	T getMultiblockLogic();

	interface Listener extends IGreenhouseComponent {
		IGreenhouseListener getGreenhouseListener();
	}
	
	interface Door extends IGreenhouseComponent {
	}

	interface Climatiser extends IGreenhouseComponent {
		void changeClimate(int tickCount, IGreenhouseController controller);
	}
	
	interface ButterflyHatch extends IGreenhouseComponent {
		ItemStack[] addCocoonLoot(IButterflyCocoon cocoon);
	}

	interface Active extends IGreenhouseComponent {
		void updateServer(int tickCount);

		void updateClient(int tickCount);
	}

}
