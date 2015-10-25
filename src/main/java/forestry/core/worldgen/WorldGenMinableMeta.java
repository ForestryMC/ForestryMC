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
package forestry.core.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

public class WorldGenMinableMeta extends WorldGenerator {

	private final Block mineableBlock;
	private final int mineableBlockMeta;
	private final int numberOfBlocks;

	public WorldGenMinableMeta(ForestryBlock block, int meta, int numberOfBlocks) {
		mineableBlock = block.block();
		mineableBlockMeta = meta;
		this.numberOfBlocks = numberOfBlocks;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {

		boolean hasGenerated = false;

		float randomBase = random.nextFloat() * 3.141593F;
		double d = (pos.getX() + 8) + (MathHelper.sin(randomBase) * numberOfBlocks) / 8F;
		double d1 = (pos.getX() + 8) - (MathHelper.sin(randomBase) * numberOfBlocks) / 8F;
		double d2 = (pos.getZ() + 8) + (MathHelper.cos(randomBase) * numberOfBlocks) / 8F;
		double d3 = (pos.getZ() + 8) - (MathHelper.cos(randomBase) * numberOfBlocks) / 8F;
		double d4 = (pos.getY() + random.nextInt(3)) - 2;
		double d5 = (pos.getY() + random.nextInt(3)) - 2;

		for (int l = 0; l <= numberOfBlocks; l++) {
			double d6 = d + ((d1 - d) * l) / numberOfBlocks;
			double d7 = d4 + ((d5 - d4) * l) / numberOfBlocks;
			double d8 = d2 + ((d3 - d2) * l) / numberOfBlocks;
			double d9 = (random.nextDouble() * numberOfBlocks) / 16D;
			double d10 = (MathHelper.sin((l * 3.141593F) / numberOfBlocks) + 1.0F) * d9 + 1.0D;
			double d11 = (MathHelper.sin((l * 3.141593F) / numberOfBlocks) + 1.0F) * d9 + 1.0D;
			int xStart = MathHelper.floor_double(d6 - d10 / 2D);
			int yStart = MathHelper.floor_double(d7 - d11 / 2D);
			int zStart = MathHelper.floor_double(d8 - d10 / 2D);
			int xEnd = MathHelper.floor_double(d6 + d10 / 2D);
			int yEnd = MathHelper.floor_double(d7 + d11 / 2D);
			int zEnd = MathHelper.floor_double(d8 + d10 / 2D);

			for (int targetX = xStart; targetX <= xEnd; targetX++) {
				double d12 = ((targetX + 0.5D) - d6) / (d10 / 2D);
				if (d12 * d12 >= 1.0D) {
					continue;
				}

				for (int targetY = yStart; targetY <= yEnd; targetY++) {
					double d13 = ((targetY + 0.5D) - d7) / (d11 / 2D);
					if (d12 * d12 + d13 * d13 >= 1.0D) {
						continue;
					}

					for (int targetZ = zStart; targetZ <= zEnd; targetZ++) {
						double d14 = ((targetZ + 0.5D) - d8) / (d10 / 2D);
						BlockPos posNEW = new BlockPos(targetX, targetY, targetZ);
						if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D
								&& world.getBlockState(pos).getBlock() == Blocks.stone) {
							world.setBlockState(pos, mineableBlock.getStateFromMeta(mineableBlockMeta),
									Defaults.FLAG_BLOCK_SYNCH);
							hasGenerated = true;
						}
					}
				}
			}
		}

		return hasGenerated;
	}

}
