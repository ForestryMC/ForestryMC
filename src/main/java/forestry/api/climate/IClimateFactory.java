/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import forestry.api.core.ILocatable;

/**
 * @since Forestry 5.8.1
 */
public interface IClimateFactory {
	/**
	 * Creates a {@link IClimateTransformer}.
	 */
	IClimateTransformer createTransformer(IClimateHousing housing);

	/**
	 * Creates a {@link IClimateListener}.
	 */
	IClimateListener createListener(ILocatable locatable);
}
