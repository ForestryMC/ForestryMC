/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst

package forestry.farming.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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
	public boolean generate(World world, Random random, int x, int y, int z) {
		Block type = mushroom;

		if (type == null) {
			type = random.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
		}

		int height = random.nextInt(1) + 2;
		boolean flag = true;
		if (y < 1 || y + height + 1 > Defaults.WORLD_HEIGHT)
			return false;

		for (int i = y; i <= y + 1 + height; i++) {
			byte offset = 3;
			if (i == y)
				offset = 0;

			for (int j = x - offset; j <= x + offset && flag; j++)
				for (int k = z - offset; k <= z + offset && flag; k++)
					if (i >= 0 && i < Defaults.WORLD_HEIGHT) {
						Block block = world.getBlock(j, i, k);
						if (!block.isAir(world, j, i, k) && block != Blocks.leaves)
							flag = false;
					} else
						flag = false;
		}

		if (!flag)
			return false;

		Block ground = world.getBlock(x, y - 1, z);
		if (ground != Blocks.mycelium)
			return false;

		if (!Blocks.brown_mushroom.canPlaceBlockAt(world, x, y, z))
			return false;

		func_150515_a(world, x, y - 1, z, Blocks.dirt);

		int capStartY = y + height;
		if (type == Blocks.red_mushroom_block)
			capStartY = (y + height) - 1;

		for (int i = capStartY; i <= y + height; i++) {

			int capRad = 1;

			if (type == Blocks.brown_mushroom_block)
				capRad = 1;

			for (int j = x - capRad; j <= x + capRad; j++)
				for (int k = z - capRad; k <= z + capRad; k++) {
					int remain = 5;
					if (j == x - capRad)
						remain--;

					if (j == x + capRad)
						remain++;

					if (k == z - capRad)
						remain -= 3;

					if (k == z + capRad)
						remain += 3;

					if (type == Blocks.brown_mushroom_block || i < y + height) {

						if (j == x - (capRad - 1) && k == z - capRad)
							remain = 1;

						if (j == x - capRad && k == z - (capRad - 1))
							remain = 1;

						if (j == x + (capRad - 1) && k == z - capRad)
							remain = 3;

						if (j == x + capRad && k == z - (capRad - 1))
							remain = 3;

						if (j == x - (capRad - 1) && k == z + capRad)
							remain = 7;

						if (j == x - capRad && k == z + (capRad - 1))
							remain = 7;

						if (j == x + (capRad - 1) && k == z + capRad)
							remain = 9;

						if (j == x + capRad && k == z + (capRad - 1))
							remain = 9;

					}
					if (remain == 5 && i < y + height)
						remain = 0;

					if ((remain != 0 || y >= (y + height) - 1) && !world.getBlock(j, i, k).isOpaqueCube())
						setBlockAndNotifyAdequately(world, j, i, k, type, remain);

				}

		}

		for (int i = 0; i < height; i++) {
			Block block = world.getBlock(x, y + i, z);
			if (!block.isOpaqueCube())
				setBlockAndNotifyAdequately(world, x, y + i, z, type, 10);
		}

		return true;
	}
}
