/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlockPosPredicate {
	boolean test(Level world, BlockPos blockPos);
}
