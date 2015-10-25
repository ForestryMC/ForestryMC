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
import net.minecraft.block.state.IBlockState;
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
		Block type = mushroom;

		if (type == null) {
			type = random.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
		}

		int height = random.nextInt(1) + 2;
		boolean flag = true;
		if (pos.getY() < 1 || pos.getY() + height + 1 > Defaults.WORLD_HEIGHT) {
			return false;
		}

		for (int i = pos.getY(); i <= pos.getY() + 1 + height; i++) {
			byte offset = 3;
			if (i == pos.getY()) {
				offset = 0;
			}

			for (int j = pos.getX() - offset; j <= pos.getX() + offset && flag; j++) {
				for (int k = pos.getZ() - offset; k <= pos.getZ() + offset && flag; k++) {
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

		Block ground = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock();
		if (ground != Blocks.mycelium) {
			return false;
		}

		if (!Blocks.brown_mushroom.canPlaceBlockAt(world, pos)) {
			return false;
		}

		setBlock(world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), Blocks.dirt);

		int capStartY = pos.getY() + height;
		if (type == Blocks.red_mushroom_block) {
			capStartY = (pos.getY() + height) - 1;
		}

		for (int i = capStartY; i <= pos.getY() + height; i++) {

			int capRad = 1;

			if (type == Blocks.brown_mushroom_block) {
				capRad = 1;
			}

			for (int j = pos.getX() - capRad; j <= pos.getX() + capRad; j++) {
				for (int k = pos.getZ() - capRad; k <= pos.getZ() + capRad; k++) {
					int remain = 5;
					if (j == pos.getX() - capRad) {
						remain--;
					}

					if (j == pos.getX() + capRad) {
						remain++;
					}

					if (k == pos.getZ() - capRad) {
						remain -= 3;
					}

					if (k == pos.getZ() + capRad) {
						remain += 3;
					}

					if (type == Blocks.brown_mushroom_block || i < pos.getY() + height) {

						if (j == pos.getX() - (capRad - 1) && k == pos.getZ() - capRad) {
							remain = 1;
						}

						if (j == pos.getX() - capRad && k == pos.getZ() - (capRad - 1)) {
							remain = 1;
						}

						if (j == pos.getX() + (capRad - 1) && k == pos.getZ() - capRad) {
							remain = 3;
						}

						if (j == pos.getX() + capRad && k == pos.getZ() - (capRad - 1)) {
							remain = 3;
						}

						if (j == pos.getX() - (capRad - 1) && k == pos.getZ() + capRad) {
							remain = 7;
						}

						if (j == pos.getX() - capRad && k == pos.getZ() + (capRad - 1)) {
							remain = 7;
						}

						if (j == pos.getX() + (capRad - 1) && k == pos.getZ() + capRad) {
							remain = 9;
						}

						if (j == pos.getX() + capRad && k == pos.getZ() + (capRad - 1)) {
							remain = 9;
						}

					}
					if (remain == 5 && i < pos.getY() + height) {
						remain = 0;
					}

					if ((remain != 0 || pos.getY() >= (pos.getY() + height) - 1)
							&& !world.getBlockState(new BlockPos(j, i, k)).getBlock().isOpaqueCube()) {
						IBlockState state = world.getBlockState(new BlockPos(j, i, k));
						setBlockAndNotifyAdequately(world, new BlockPos(j, i, k),
								state.getBlock().getStateFromMeta(remain));
					}

				}
			}

		}

		for (int i = 0; i < height; i++) {
			Block block = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + i, pos.getZ())).getBlock();
			if (!block.isOpaqueCube()) {
				IBlockState state = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + i, pos.getZ()));
				setBlockAndNotifyAdequately(world, new BlockPos(pos.getX(), pos.getY() + i, pos.getZ()),
						state.getBlock().getStateFromMeta(10));
			}
		}

		return true;
	}
}
