/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ClimateCapabilities {
	/**
	 * Capability for {@link IClimateListener}.
	 */
	@CapabilityInject(IClimateListener.class)
	public static Capability<IClimateListener> CLIMATE_LISTENER;
	/**
	 * Capability for {@link IClimateTransformer}.
	 */
	@CapabilityInject(IClimateListener.class)
	public static Capability<IClimateTransformer> CLIMATE_TRANSFORMER;
}
