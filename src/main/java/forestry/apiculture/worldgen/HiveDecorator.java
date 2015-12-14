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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.plugins.PluginApiculture;

public abstract class HiveDecorator {

	@SuppressWarnings("rawtypes")
	private static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_HIVES", new Class[0], new Object[0]);

	public static void decorateHives(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (!TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, EVENT_TYPE)) {
			return;
		}

		decorateHives(world, rand, chunkX, chunkZ);
	}

	public static void decorateHives(World world, Random rand, int chunkX, int chunkZ) {
		List<Hive> hives = PluginApiculture.hiveRegistry.getHives();

		if (Config.generateBeehivesDebug) {
			decorateHivesDebug(world, chunkX, chunkZ, hives);
			return;
		}

		Collections.shuffle(hives, rand);
		for (Hive hive : hives) {
			if (genHive(world, rand, chunkX, chunkZ, hive)) {
				return;
			}
		}
	}

	public static boolean genHive(World world, Random rand, int chunkX, int chunkZ, Hive hive) {
		if (hive.genChance() * Config.getBeehivesAmount() < rand.nextFloat() * 100.0f) {
			return false;
		}

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;

		BiomeGenBase biome = world.getBiomeGenForCoords(worldX, worldZ);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
			return false;
		}

		for (int tries = 0; tries < 4; tries++) {
			int x = worldX + rand.nextInt(16);
			int z = worldZ + rand.nextInt(16);

			if (tryGenHive(world, x, z, hive)) {
				return true;
			}
		}

		return false;
	}

	private static void decorateHivesDebug(World world, int chunkX, int chunkZ, List<Hive> hives) {
		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;
		BiomeGenBase biome = world.getBiomeGenForCoords(worldX, worldZ);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Collections.shuffle(hives, world.rand);
				for (Hive hive : hives) {
					if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
						continue;
					}

					tryGenHive(world, worldX + x, worldZ + z, hive);
				}
			}
		}
	}

	private static boolean tryGenHive(World world, int x, int z, Hive hive) {

		int y = hive.getYForHive(world, x, z);

		if (y < 0) {
			return false;
		}

		if (!hive.canReplace(world, x, y, z)) {
			return false;
		}

		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		EnumTemperature temperature = EnumTemperature.getFromValue(biome.getFloatTemperature(x, y, z));
		if (!hive.isGoodTemperature(temperature)) {
			return false;
		}

		if (!hive.isValidLocation(world, x, y, z)) {
			return false;
		}

		return setHive(world, x, y, z, hive);
	}

	private static boolean setHive(World world, int x, int y, int z, Hive hive) {
		Block hiveBlock = hive.getHiveBlock();
		boolean placed = world.setBlock(x, y, z, hiveBlock, hive.getHiveMeta(), Constants.FLAG_BLOCK_SYNCH);
		if (!placed) {
			return false;
		}

		Block placedBlock = world.getBlock(x, y, z);
		if (!Block.isEqualTo(hiveBlock, placedBlock)) {
			return false;
		}

		hiveBlock.onBlockAdded(world, x, y, z);
		world.markBlockForUpdate(x, y, z);

		if (!Config.generateBeehivesDebug) {
			hive.postGen(world, x, y, z);
		}
		return true;
	}
}
