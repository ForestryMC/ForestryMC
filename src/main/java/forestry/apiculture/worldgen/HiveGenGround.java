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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class HiveGenGround extends HiveGen {

	private final Set<Material> groundMaterials = new HashSet<Material>();

	public HiveGenGround(Block... groundBlocks) {
		for (Block block : groundBlocks) {
			groundMaterials.add(block.getMaterial());
		}
	}

	@Override
	public boolean isValidLocation(World world, BlockPos pos) {
		Block ground = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock();
		return groundMaterials.contains(ground.getMaterial());
	}

	@Override
	public int getYForHive(World world, BlockPos pos) {
		pos = world.getHeight(pos);

		// get to the ground
		while (pos.getY() >= 0 && (world.getBlockState(pos).getBlock().isLeaves(world, pos) || canReplace(world, pos))) {
			pos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
		}

		return pos.getY();
	}
}
