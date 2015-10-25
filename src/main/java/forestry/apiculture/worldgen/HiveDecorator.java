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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.plugins.PluginApiculture;

public abstract class HiveDecorator {

	@SuppressWarnings("rawtypes")
	private static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_HIVES", new Class[0],
			new Object[0]);

	public static void decorateHives(IChunkProvider chunkProvider, World world, Random rand, BlockPos pos,
			boolean hasVillageGenerated) {
		if (!TerrainGen.populate(chunkProvider, world, rand, pos.getX(), pos.getZ(), hasVillageGenerated, EVENT_TYPE)) {
			return;
		}

		decorateHives(world, rand, new BlockPos(pos.getX(), 0, pos.getZ()));
	}

	public static void decorateHives(World world, Random rand, BlockPos posChunk) {
		List<Hive> hives = PluginApiculture.hiveRegistry.getHives();

		if (Config.generateBeehivesDebug) {
			decorateHivesDebug(world, posChunk, hives);
			return;
		}

		Collections.shuffle(hives, rand);
		for (Hive hive : hives) {
			if (genHive(world, rand, posChunk, hive)) {
				return;
			}
		}
	}

	public static boolean genHive(World world, Random rand, BlockPos posChunk, Hive hive) {
		if (hive.genChance() * Config.getBeehivesAmount() < rand.nextFloat() * 100.0f) {
			return false;
		}

		int worldX = posChunk.getX() * 16;
		int worldZ = posChunk.getZ() * 16;

		BiomeGenBase biome = world.getBiomeGenForCoords(new BlockPos(worldX, 0, worldZ));
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
			return false;
		}

		for (int tries = 0; tries < 4; tries++) {
			int x = worldX + rand.nextInt(16);
			int z = worldZ + rand.nextInt(16);

			if (tryGenHive(world, new BlockPos(x, 0, z), hive)) {
				return true;
			}
		}

		return false;
	}

	private static void decorateHivesDebug(World world, BlockPos posChunk, List<Hive> hives) {
		int worldX = posChunk.getX() * 16;
		int worldZ = posChunk.getZ() * 16;
		BiomeGenBase biome = world.getBiomeGenForCoords(new BlockPos(worldX, 0, worldZ));
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Collections.shuffle(hives, world.rand);
				for (Hive hive : hives) {
					if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
						continue;
					}

					tryGenHive(world, new BlockPos(worldX + x, 0, worldZ + z), hive);
				}
			}
		}
	}

	private static boolean tryGenHive(World world, BlockPos posChunk, Hive hive) {

		int y = hive.getYForHive(world, posChunk);

		if (y < 0) {
			return false;
		}

		BlockPos pos = new BlockPos(posChunk.getX(), y, posChunk.getZ());
		if (!hive.canReplace(world, posChunk)) {
			return false;
		}

		BiomeGenBase biome = world.getBiomeGenForCoords(pos);
		EnumTemperature temperature = EnumTemperature.getFromValue(biome.getFloatTemperature(posChunk));
		if (!hive.isGoodTemperature(temperature)) {
			return false;
		}

		if (!hive.isValidLocation(world, posChunk)) {
			return false;
		}

		return setHive(world, pos, hive);
	}

	private static boolean setHive(World world, BlockPos pos, Hive hive) {
		Block hiveBlock = hive.getHiveBlock();
		boolean placed = world.setBlockState(pos, hiveBlock.getStateFromMeta(hive.getHiveMeta()),
				Defaults.FLAG_BLOCK_SYNCH);
		if (!placed) {
			return false;
		}

		Block placedBlock = world.getBlockState(pos).getBlock();
		if (!Block.isEqualTo(hiveBlock, placedBlock)) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		hiveBlock.onBlockAdded(world, pos, state);
		world.markBlockForUpdate(pos);

		if (!Config.generateBeehivesDebug) {
			hive.postGen(world, pos);
		}
		return true;
	}
}
