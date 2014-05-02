/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.common.IPlantable;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

public abstract class WorldGenHive extends WorldGenerator {

	@Override
	public abstract boolean generate(World world, Random random, int x, int y, int z);

	protected boolean tryPlaceTreeHive(World world, int x, int y, int z, int meta) {
		if (!world.isAirBlock(x, y, z))
			return false;

		Block b = world.getBlock(x, y + 1, z);
		if (b == null || !b.isLeaves(world, x, y + 1, z))
			return false;

		if (world.isAirBlock(x, y - 1, z)) {
			setHive(world, x, y, z, meta);
			return true;
		}
		return false;
	}

	protected boolean tryPlaceGroundHive(World world, int x, int y, int z, int meta, Block... groundBlocks) {
		if (!world.isAirBlock(x, y, z)) {
			Block block = world.getBlock(x, y, z);
			if (block != Blocks.snow_layer && !(block instanceof IPlantable))
				return false;
		}

		if (!world.isAirBlock(x, y + 1, z))
			return false;

		if (isAcceptableBlock(world, x, y - 1, z, groundBlocks)) {
			setHive(world, x, y, z, meta);
			return true;
		}

		return false;
	}

	private boolean isAcceptableBlock(World world, int x, int y, int z, Block... blocks) {
		Block block = world.getBlock(x, y, z);
		if (block == null)
			return false;
		for (Block testBlock : blocks) {
			if (block.isReplaceableOreGen(world, x, y, z, testBlock))
				return true;
		}
		return false;
	}

	protected void setHive(World world, int x, int y, int z, int meta) {
		boolean placed = world.setBlock(x, y, z, ForestryBlock.beehives, meta, Defaults.FLAG_BLOCK_SYNCH);
		if (!placed)
			return;

		if (world.getBlock(x, y, z) != ForestryBlock.beehives)
			return;

		ForestryBlock.beehives.onBlockAdded(world, x, y, z);
		world.markBlockForUpdate(x, y, z);

		postGen(world, x, y, z, meta);
	}

	protected void postGen(World world, int x, int y, int z, int meta) {
		ForestryBlock.beehives.onBlockPlaced(world, x, y, z, 0, 0.0f, 0.0f, 0.0f, meta);
	}
}
