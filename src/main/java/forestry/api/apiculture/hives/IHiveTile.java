/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IHiveTile {
	/**
	 * Call to calm agitated bees. Used by the smoker to stop wild bees from attacking as much.
	 * Bees will not stay calm for very long.
	 */
	void calmBees();

	boolean isAngry();

	/**
	 * Called when the hive is attacked.
	 */
	void onAttack(Level world, BlockPos pos, Player player);

	/**
	 * Called when the hive is broken.
	 */
	void onBroken(Level world, BlockPos pos, Player player, boolean canHarvest);
}
