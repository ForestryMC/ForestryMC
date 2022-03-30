/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import forestry.api.arboriculture.genetics.ITree;

public interface IGrowthProvider {

	boolean canSpawn(ITree tree, Level world, BlockPos pos);

	boolean isBiomeValid(ITree tree, Biome biome);

}
