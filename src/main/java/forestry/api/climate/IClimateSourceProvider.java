/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.Set;

import forestry.api.core.ILocatable;
import net.minecraft.util.math.BlockPos;

public interface IClimateSourceProvider extends ILocatable {

	IClimateSource getClimateSource();
	
	Set<BlockPos> getPositionsInRange();
}
