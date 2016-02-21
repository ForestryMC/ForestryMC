/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.world;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.ITreeGenome;

public interface ITreeGenData {

	int getGirth(World world, BlockPos pos);

	float getHeightModifier();

	boolean canGrow(World world, BlockPos pos, int expectedGirth, int expectedHeight);

	void setLeaves(World world, GameProfile owner, BlockPos pos);
	void setLeavesDecorative(World world, GameProfile owner, BlockPos pos);

	void setLogBlock(World world, BlockPos pos, EnumFacing facing);

	boolean allowsFruitBlocks();

	boolean trySpawnFruitBlock(World world, BlockPos pos);

	ITreeGenome getGenome();
}
