/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.worldgen;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HiveGenGround extends HiveGen {

	private final Set<Material> groundMaterials = new HashSet<>();

	public HiveGenGround(Block... groundBlocks) {
		for (Block block : groundBlocks) {
			IBlockState blockState = block.getDefaultState();
			Material blockMaterial = block.getMaterial(blockState);
			groundMaterials.add(blockMaterial);
		}
	}

	@Override
	public boolean isValidLocation(World world, BlockPos pos) {
		IBlockState groundBlockState = world.getBlockState(pos.down());
		Block groundBlock = groundBlockState.getBlock();
		Material groundBlockMaterial = groundBlock.getMaterial(groundBlockState);
		return groundMaterials.contains(groundBlockMaterial);
	}

	@Override
	public int getYForHive(World world, int x, int z) {

		// get to the ground
		BlockPos pos = world.getHeight(new BlockPos(x, 0, z));
		IBlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		while (pos.getY() >= 0 && (block.isLeaves(blockState, world, pos.down()) || canReplace(blockState, world, pos.down()))) {
			pos = pos.down();
			blockState = world.getBlockState(pos);
			block = blockState.getBlock();
		}

		return pos.getY();
	}
}
