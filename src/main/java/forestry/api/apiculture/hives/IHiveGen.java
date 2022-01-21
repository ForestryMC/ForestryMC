/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

public interface IHiveGen {

	/**
	 * return a position that the hive should try to generate at.
	 * returns null if the hive can't be placed anywhere.
	 */
	@Nullable
	BlockPos getPosForHive(WorldGenLevel world, int x, int z);

	/**
	 * returns true if the hive can be generated at this location.
	 * Used for advanced conditions, like checking that the ground below the hive is a certain type.
	 */
	boolean isValidLocation(WorldGenLevel world, BlockPos pos);

	/**
	 * returns true if the hive can safely replace the block at this location.
	 */
	boolean canReplace(BlockState blockState, WorldGenLevel world, BlockPos pos);

}
