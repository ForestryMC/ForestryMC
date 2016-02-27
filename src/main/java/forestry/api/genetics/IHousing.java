/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

/**
 * Any housing, hatchery or nest with a location in the world.
 */
public interface IHousing {

	BlockPos getCoordinates();

	World getWorld();

	GameProfile getOwner();

}
