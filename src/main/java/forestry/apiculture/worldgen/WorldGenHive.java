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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

public abstract class WorldGenHive extends WorldGenerator {

	@Override
	public abstract boolean generate(World world, Random random, int x, int y, int z);

	protected boolean tryPlaceTreeHive(World world, int x, int z, int meta) {

		int y = getTopLeafHeight(world, x, z);
		if (y == -1)
			return false;

		// get to the bottom of the leaves
		do { y--; } while (world.getBlock(x, y, z).isLeaves(world, x, y, z));

		if (isReplaceableByHive(world, x, y, z)) {
			setHive(world, x, y, z, meta);
			return true;
		}

		return false;
	}

	private int getTopLeafHeight(World world, int x, int z) {
		int y = world.getHeightValue(x, z) - 1;
		if (world.getBlock(x, y, z).isLeaves(world, x, y, z))
			return y;
		return -1;
	}

	protected boolean tryPlaceGroundHive(World world, int x, int z, int meta, Block... groundBlocks) {

		int y = world.getHeightValue(x, z);

		if (isReplaceableByHive(world, x, y, z) && isAcceptableGround(world, x, y - 1, z, groundBlocks)) {
			setHive(world, x, y, z, meta);
			return true;
		}
		return false;
	}

	protected boolean isReplaceableByHive(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		Material material = block.getMaterial();
		return (material.isReplaceable() && !material.isLiquid()) ||
				material == Material.air ||
				material == Material.grass ||
				material == Material.plants;
	}

	private boolean isAcceptableGround(World world, int x, int y, int z, Block... blocks) {
		Block block = world.getBlock(x, y, z);
		if (block == null)
			return false;
		for (Block testBlock : blocks)
			if (block.getMaterial() == testBlock.getMaterial())
				return true;
		return false;
	}

	protected void setHive(World world, int x, int y, int z, int meta) {
		boolean placed = world.setBlock(x, y, z, ForestryBlock.beehives.block(), meta, Defaults.FLAG_BLOCK_SYNCH);
		if (!placed)
			return;

		if (!ForestryBlock.beehives.isBlockEqual(world, x, y, z))
			return;

		ForestryBlock.beehives.block().onBlockAdded(world, x, y, z);
		world.markBlockForUpdate(x, y, z);

		postGen(world, x, y, z, meta);
	}

	protected void postGen(World world, int x, int y, int z, int meta) {
		ForestryBlock.beehives.block().onBlockPlaced(world, x, y, z, 0, 0.0f, 0.0f, 0.0f, meta);
	}
}
