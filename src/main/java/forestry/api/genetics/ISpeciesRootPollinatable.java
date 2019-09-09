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

import genetics.api.individual.IIndividual;

/**
 * @author Nedelosk
 * @since 5.12.16
 */
//TODO: Move to a component ?
public interface ISpeciesRootPollinatable<I extends IIndividual> extends IForestrySpeciesRoot<I> {

	ICheckPollinatable createPollinatable(IIndividual individual);

	@Nullable
	IPollinatable tryConvertToPollinatable(@Nullable GameProfile owner, World world, final BlockPos pos, final IIndividual pollen);

}
