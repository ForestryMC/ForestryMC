/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IHiveGen {

	/**
	 * return the passed in BlockPos with an adjusted Y value, representing where the hive should try to generate at.
	 * sets the Y value to negative if the hive can't be placed anywhere.
	 */
	BlockPos getYForHive(World world, BlockPos pos);

	/**
	 * returns true if the hive can be generated at this location.
	 * Used for advanced conditions, like checking that the ground below the hive is a certain type.
	 */
	boolean isValidLocation(World world, BlockPos pos);

	/**
	 * returns true if the hive can safely replace the block at this location.
	 */
	boolean canReplace(World world, BlockPos pos);

}
