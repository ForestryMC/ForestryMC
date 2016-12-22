/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.world;

import javax.annotation.Nullable;
import java.util.Random;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.ITreeGenome;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITreeGenData {

	int getGirth();

	float getHeightModifier();

	/**
	 * @return Position that this tree can grow. May be different from pos if there are multiple saplings.
	 * Returns null if a sapling at the given position can not grow into a tree.
	 */
	@Nullable
	BlockPos canGrow(World world, BlockPos pos, int expectedGirth, int expectedHeight);

	boolean setLeaves(World world, @Nullable GameProfile owner, BlockPos pos);

	boolean setLogBlock(World world, BlockPos pos, EnumFacing facing);

	boolean allowsFruitBlocks();

	boolean trySpawnFruitBlock(World world, Random rand, BlockPos pos);

	ITreeGenome getGenome();
}
