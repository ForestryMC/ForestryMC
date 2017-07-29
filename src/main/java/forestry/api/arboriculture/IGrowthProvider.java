/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IGrowthProvider {

	boolean canSpawn(ITree tree, World world, BlockPos pos);
	
	boolean isBiomeValid(ITree tree, Biome biome);
	
}
