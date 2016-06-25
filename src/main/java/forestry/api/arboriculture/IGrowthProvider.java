/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @deprecated this is being removed to simplify trees
 */
@Deprecated
public interface IGrowthProvider {

	/**
	 * Check to see whether a sapling at the given location with the given genome can grow into a tree.
	 *
	 * @param genome Genome of the tree this is called for.
	 * @param world Minecraft world the tree will inhabit.
	 * @param pos Coordinates to attempt growth at.
	 * @param expectedGirth Trunk size of the tree to generate.
	 * @param expectedHeight Height of the tree to generate.
	 * @return position that the tree can grow, null otherwise.
	 */
	@Nullable
	BlockPos canGrow(ITreeGenome genome, World world, BlockPos pos, int expectedGirth, int expectedHeight);

	/**
	 * @return Short, human-readable identifier used in the treealyzer.
	 */
	String getDescription();

	/**
	 * @return Detailed description of growth behaviour used in the treealyzer.
	 */
	String[] getInfo();

}
