/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.world;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

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
	BlockPos canGrow(IWorld world, BlockPos pos, int expectedGirth, int expectedHeight);

	boolean setLeaves(IWorld world, @Nullable GameProfile owner, BlockPos pos, Random random);

	boolean setLogBlock(IWorld world, BlockPos pos, Direction facing);

	boolean allowsFruitBlocks();

	boolean trySpawnFruitBlock(IWorld world, Random rand, BlockPos pos);

	IGenome getGenome();
}
