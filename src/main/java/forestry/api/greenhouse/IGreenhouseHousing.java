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

	float getExactTemperature();

	float getExactHumidity();

	/**
	 * Must not be named "getWorld" to avoid SpecialSource issue https://github.com/md-5/SpecialSource/issues/12
	 *
	 * @return The world in that the housing is.
	 */
	World getWorldObj();

}
