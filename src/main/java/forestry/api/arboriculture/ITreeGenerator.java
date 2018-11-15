/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;

/**
 * Implements the tree generation for a tree species.
 */
public interface ITreeGenerator {
	WorldGenerator getWorldGenerator(ITreeGenData tree);

	boolean setLogBlock(ITreeGenome genome, World world, BlockPos pos, EnumFacing facing);

	@Deprecated
	boolean setLeaves(ITreeGenome genome, World world, @Nullable GameProfile owner, BlockPos pos);

	default boolean setLeaves(ITreeGenome genome, World world, @Nullable GameProfile owner, BlockPos pos, Random rand) {
		return setLeaves(genome, world, owner, pos);
	}
}
