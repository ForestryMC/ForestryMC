/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;

import forestry.greenhouse.api.climate.IGreenhouseClimateManager;

public class GreenhouseManager {

	@Nullable
	public static IGreenhouseHelper helper;
	@Nullable
	public static IGreenhouseBlockManager blockManager;
	@Nullable
	public static IGreenhouseClimateManager climateManager;

}
