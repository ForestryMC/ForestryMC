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
 * @author Nedelosk
 * @since 5.12.16
 */
public interface ISpeciesRootPollinatable extends ISpeciesRoot {

	ICheckPollinatable createPollinatable(IIndividual individual);

	@Nullable
	IPollinatable tryConvertToPollinatable(@Nullable GameProfile owner, World world, final BlockPos pos, final IIndividual pollen);

}
