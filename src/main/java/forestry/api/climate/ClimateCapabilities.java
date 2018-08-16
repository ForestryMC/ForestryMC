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
