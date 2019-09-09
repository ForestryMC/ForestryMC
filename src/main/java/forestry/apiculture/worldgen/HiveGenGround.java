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
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public class HiveGenGround extends HiveGen {

	private final Set<Material> groundMaterials = new HashSet<>();

	public HiveGenGround(Block... groundBlocks) {
		for (Block block : groundBlocks) {
			BlockState blockState = block.getDefaultState();
			Material blockMaterial = blockState.getMaterial();
			groundMaterials.add(blockMaterial);
		}
	}

	@Override
	public boolean isValidLocation(World world, BlockPos pos) {
		BlockState groundBlockState = world.getBlockState(pos.down());
		Material groundBlockMaterial = groundBlockState.getMaterial();
		return groundMaterials.contains(groundBlockMaterial);
	}

	@Override
	public BlockPos getPosForHive(World world, int x, int z) {
		// getComb to the ground
		final BlockPos topPos = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(x, 0, z));
		if (topPos.getY() == 0) {
			return null;
		}

		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(topPos);

		BlockState blockState = world.getBlockState(pos);
		while (isTreeBlock(blockState, world, pos) || canReplace(blockState, world, pos)) {
			pos.move(Direction.DOWN);
			if (pos.getY() <= 0) {
				return null;
			}
			blockState = world.getBlockState(pos);
		}

		return pos.up();
	}
}
