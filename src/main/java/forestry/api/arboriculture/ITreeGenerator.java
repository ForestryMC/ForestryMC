/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.authlib.GameProfile;

import genetics.api.individual.IGenome;

/**
 * Implements the tree generation for a tree species.
 */
public interface ITreeGenerator {
	Feature<NoneFeatureConfiguration> getTreeFeature(ITreeGenData tree);

	boolean setLogBlock(IGenome genome, LevelAccessor world, BlockPos pos, Direction facing);

	boolean setLeaves(IGenome genome, LevelAccessor world, @Nullable GameProfile owner, BlockPos pos, Random rand);
}
