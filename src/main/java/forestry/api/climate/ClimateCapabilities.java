/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ClimateCapabilities {
	/**
	 * Capability for {@link IClimateListener}.
	 */
	public static Capability<IClimateListener> CLIMATE_LISTENER = CapabilityManager.get(new CapabilityToken<>() {});
	/**
	 * Capability for {@link IClimateTransformer}.
	 */
	public static Capability<IClimateTransformer> CLIMATE_TRANSFORMER = CapabilityManager.get(new CapabilityToken<>() {});
}
