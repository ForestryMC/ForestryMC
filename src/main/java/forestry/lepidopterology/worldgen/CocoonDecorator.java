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
package forestry.lepidopterology.worldgen;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.Constants;
import forestry.core.utils.BlockUtil;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.tiles.TileCocoon;

public abstract class CocoonDecorator {

	private static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_COCOONS", new Class[0], new Object[0]);

	public static void decorateCocoons(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (!TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, EVENT_TYPE)) {
			return;
		}

		decorateCocoons(world, rand, chunkX, chunkZ);
	}

	public static void decorateCocoons(World world, Random rand, int chunkX, int chunkZ) {
		List<IButterfly> butterflys = ButterflyManager.butterflyRoot.getIndividualTemplates();

		Collections.shuffle(butterflys, rand);
		for (IButterfly butterfly : butterflys) {
			if (genCocoon(world, rand, chunkX, chunkZ, butterfly)) {
				return;
			}
		}
	}

	public static boolean genCocoon(World world, Random rand, int chunkX, int chunkZ, IButterfly butterfly) {
		if (butterfly.getGenome().getPrimary().getRarity() * PluginLepidopterology.getGenerateCocoonsAmount() < rand.nextFloat() * 100.0f) {
			return false;
		}

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;

		BiomeGenBase biome = world.getBiomeGenForCoords(new BlockPos(worldX, 0, worldZ));
		
		Type[] types = BiomeDictionary.getTypesForBiome(biome);
		EnumSet<Type> speciesTypes = butterfly.getGenome().getPrimary().getSpawnBiomes();

		boolean biomeTypesGood = false;
		for (Type type : types) {
			if (speciesTypes == null || speciesTypes.size() <= 0 || speciesTypes.contains(type)) {
				biomeTypesGood = true;
			}
		}
		if (!biomeTypesGood) {
			return false;
		}

		for (int tries = 0; tries < 4; tries++) {
			int x = worldX + rand.nextInt(16);
			int z = worldZ + rand.nextInt(16);

			if (tryGenCocoon(world, x, z, butterfly)) {
				return true;
			}
		}

		return false;
	}

	private static boolean tryGenCocoon(World world, int x, int z, IButterfly butterfly) {

		int y = getYForCocoon(world, x, z);

		if (y < 0) {
			return false;
		}

		if (!isValidLocation(world, new BlockPos(x, y, z))) {
			return false;
		}

		return setCocoon(world, new BlockPos(x, y, z), butterfly);
	}

	private static boolean setCocoon(World world, BlockPos pos, IButterfly butterfly) {
		Block cocoonBlock = PluginLepidopterology.blocks.solidCocoon;
		boolean placed = world.setBlockState(pos, cocoonBlock.getDefaultState(), Constants.FLAG_BLOCK_SYNCH);
		if (!placed) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		if (!Block.isEqualTo(cocoonBlock, state.getBlock())) {
			return false;
		}
		
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCocoon) {
			TileCocoon cocoon = (TileCocoon) tile;
			cocoon.setCaterpillar(butterfly);
		} else {
			return false;
		}

		cocoonBlock.onBlockAdded(world, pos, state);
		world.markBlockForUpdate(pos);

		return true;
	}
	
	private static int getYForCocoon(World world, int x, int z) {
		int y = world.getHeight(new BlockPos(x, 0, z)).getY() - 1;
		if (!world.getBlockState(new BlockPos(x, y, z)).getBlock().isLeaves(world, new BlockPos(x, y, z))) {
			return -1;
		}

		do {
			y--;
		} while (world.getBlockState(new BlockPos(x, y, z)).getBlock().isLeaves(world, new BlockPos(x, y, z)));

		return y;
	}
	
	public static boolean isValidLocation(World world, BlockPos pos) {
		Block blockAbove = world.getBlockState(pos.up()).getBlock();
		if (!blockAbove.isLeaves(world, pos.up())) {
			return false;
		}

		return BlockUtil.canReplace(world, pos.down());
	}
}
