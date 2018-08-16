package forestry.api.climate;

import forestry.api.core.ILocatable;

/**
 * @since Forestry 5.8.1
 */
public interface IClimateFactory {
	/**
	 * Creates a {@link IClimateLogic}.
	 */
	IClimateLogic createLogic(IClimateHousing housing);

	/**
	 * Creates a {@link IClimateListener}.
	 */
	IClimateListener createListener(ILocatable locatable);
}
