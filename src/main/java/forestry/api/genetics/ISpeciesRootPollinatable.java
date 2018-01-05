/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

/**
 * A species root that provides helper functions for pollination.
 */
public interface ISpeciesRootPollinatable extends ISpeciesRoot {

	/**
	 * Creates a {@link ICheckPollinatable} that can be used to check for pollination traits without altering the world
	 * by changing vanilla leaves to forestry ones.
	 */
	ICheckPollinatable createPollinatable(IIndividual individual);

	/**
	 * Returns an IPollinatable that can be mated.
	 */
	@Nullable
	IPollinatable tryConvertToPollinatable(@Nullable GameProfile owner, World world, final BlockPos pos, final IIndividual pollen);

}
