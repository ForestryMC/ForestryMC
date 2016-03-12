/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogicSource;
import net.minecraft.world.World;

public interface IGreenhouseHousing extends IErrorLogicSource {

	EnumTemperature getTemperature();

	EnumHumidity getHumidity();

	World getWorld();
	
}
