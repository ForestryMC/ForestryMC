/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import com.mojang.authlib.GameProfile;

import genetics.api.individual.IGenome;

public interface ITreeGenData {

	int getGirth();

	float getHeightModifier();

	/**
	 * @return Position that this tree can grow. May be different from pos if there are multiple saplings.
	 * Returns null if a sapling at the given position can not grow into a tree.
	 */
	@Nullable
	BlockPos canGrow(LevelAccessor world, BlockPos pos, int expectedGirth, int expectedHeight);

	boolean setLeaves(LevelAccessor world, @Nullable GameProfile owner, BlockPos pos, RandomSource random);

	boolean setLogBlock(LevelAccessor world, BlockPos pos, Direction facing);

	boolean allowsFruitBlocks();

	boolean trySpawnFruitBlock(LevelAccessor world, RandomSource rand, BlockPos pos);

	IGenome getGenome();
}
