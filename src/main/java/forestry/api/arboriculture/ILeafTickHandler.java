/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import forestry.api.arboriculture.genetics.ITree;

public interface ILeafTickHandler {
	boolean onRandomLeafTick(ITree tree, Level world, RandomSource rand, BlockPos pos, boolean isDestroyed);
}
