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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;

public class HiveGenGround extends HiveGen {

	private final Set<Material> groundMaterials = new HashSet<>();

	public HiveGenGround(Block... groundBlocks) {
		for (Block block : groundBlocks) {
			BlockState blockState = block.defaultBlockState();
			Material blockMaterial = blockState.getMaterial();
			groundMaterials.add(blockMaterial);
		}
	}

	@Override
	public boolean isValidLocation(WorldGenLevel world, BlockPos pos) {
		BlockState groundBlockState = world.getBlockState(pos.below());
		Material groundBlockMaterial = groundBlockState.getMaterial();
		return groundMaterials.contains(groundBlockMaterial);
	}

	@Override
	public BlockPos getPosForHive(WorldGenLevel world, int x, int z) {
		// get to the ground
		final BlockPos topPos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(x, 0, z));
		if (topPos.getY() == 0) {
			return null;
		}

		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		pos.set(topPos);

		BlockState blockState = world.getBlockState(pos);
		while (isTreeBlock(blockState) || canReplace(blockState, world, pos)) {
			pos.move(Direction.DOWN);
			if (pos.getY() <= 0) {
				return null;
			}
			blockState = world.getBlockState(pos);
		}

		return pos.above();
	}
}
