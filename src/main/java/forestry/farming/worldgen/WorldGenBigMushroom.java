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
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package forestry.farming.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.core.config.Defaults;

// Referenced classes of package net.minecraft.src:
//            WorldGenerator, World, Block, BlockLeaves,
//            BlockGrass, BlockMycelium, BlockFlower

public class WorldGenBigMushroom extends WorldGenerator {
	private final Block mushroom;

	public WorldGenBigMushroom(Block block) {
		mushroom = block;
	}

	public WorldGenBigMushroom() {
		mushroom = null;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Block type = mushroom;

		if (type == null) {
			type = random.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
		}

		int height = random.nextInt(1) + 2;
		boolean flag = true;
		if (y < 1 || y + height + 1 > Defaults.WORLD_HEIGHT) {
			return false;
		}

		for (int i = y; i <= y + 1 + height; i++) {
			byte offset = 3;
			if (i == y) {
				offset = 0;
			}

			for (int j = x - offset; j <= x + offset && flag; j++) {
				for (int k = z - offset; k <= z + offset && flag; k++) {
					if (i >= 0 && i < Defaults.WORLD_HEIGHT) {
						Block block = world.getBlockState(new BlockPos(j, i, k)).getBlock();
						if (!block.isAir(world, new BlockPos(j, i, k)) && block != Blocks.leaves) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
			}
		}

		if (!flag) {
			return false;
		}

		Block ground = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
		if (ground != Blocks.mycelium) {
			return false;
		}

		if (!Blocks.brown_mushroom.canPlaceBlockAt(world, pos)) {
			return false;
		}

		func_175906_a(world, new BlockPos(x, y - 1, z), Blocks.dirt);

		int capStartY = y + height;
		if (type == Blocks.red_mushroom_block) {
			capStartY = (y + height) - 1;
		}

		for (int i = capStartY; i <= y + height; i++) {

			int capRad = 1;

			if (type == Blocks.brown_mushroom_block) {
				capRad = 1;
			}

			for (int j = x - capRad; j <= x + capRad; j++) {
				for (int k = z - capRad; k <= z + capRad; k++) {
					int remain = 5;
					if (j == x - capRad) {
						remain--;
					}

					if (j == x + capRad) {
						remain++;
					}

					if (k == z - capRad) {
						remain -= 3;
					}

					if (k == z + capRad) {
						remain += 3;
					}

					if (type == Blocks.brown_mushroom_block || i < y + height) {

						if (j == x - (capRad - 1) && k == z - capRad) {
							remain = 1;
						}

						if (j == x - capRad && k == z - (capRad - 1)) {
							remain = 1;
						}

						if (j == x + (capRad - 1) && k == z - capRad) {
							remain = 3;
						}

						if (j == x + capRad && k == z - (capRad - 1)) {
							remain = 3;
						}

						if (j == x - (capRad - 1) && k == z + capRad) {
							remain = 7;
						}

						if (j == x - capRad && k == z + (capRad - 1)) {
							remain = 7;
						}

						if (j == x + (capRad - 1) && k == z + capRad) {
							remain = 9;
						}

						if (j == x + capRad && k == z + (capRad - 1)) {
							remain = 9;
						}

					}
					if (remain == 5 && i < y + height) {
						remain = 0;
					}

					if ((remain != 0 || y >= (y + height) - 1) && !world.getBlockState(new BlockPos(j, i, k)).getBlock().isOpaqueCube()) {
						setBlockAndNotifyAdequately(world, new BlockPos(j, i, k), type.getStateFromMeta(remain));
					}

				}
			}

		}

		for (int i = 0; i < height; i++) {
			Block block = world.getBlockState(new BlockPos(x, y + i, z)).getBlock();
			if (!block.isOpaqueCube()) {
				setBlockAndNotifyAdequately(world, new BlockPos(x, y + i, z), type.getStateFromMeta(10));
			}
		}

		return true;
	}
}
