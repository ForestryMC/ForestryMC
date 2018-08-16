/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate.source;

import forestry.api.core.ILocatable;

public interface IClimateSourceProxy<N extends IClimateSource> extends ILocatable {
	N getNode();
}
