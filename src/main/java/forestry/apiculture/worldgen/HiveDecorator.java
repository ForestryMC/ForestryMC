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

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class HiveDecorator {

	@SuppressWarnings("rawtypes")
	public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_HIVES", new Class[0], new Object[0]);
	private static HiveDecorator instance;

	public static HiveDecorator instance() {
		if (instance == null) {
			instance = new HiveDecorator();
		}
		return instance;
	}

	private HiveDecorator() {
	}

	public void decorateHives(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (!TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, EVENT_TYPE)) {
			return;
		}

		decorateHives(world, rand, chunkX, chunkZ);
	}

	public void decorateHives(World world, Random rand, int chunkX, int chunkZ) {
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

	public boolean genHive(World world, Random rand, int chunkX, int chunkZ, Hive hive) {
		if (hive.genChance() * Config.getBeehivesRate() < rand.nextFloat() * 100.0f) {
			return false;
		}

		BlockPos pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);

		BiomeGenBase biome = world.getBiomeGenForCoords(pos);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
			return false;
		}

		for (int tries = 0; tries < 4; tries++) {
			BlockPos pos1 = pos.add(rand.nextInt(16), 0, rand.nextInt(16));

			if (tryGenHive(world, pos1, hive)) {
				return true;
			}
		}

		return false;
	}

	private void decorateHivesDebug(World world, int chunkX, int chunkZ, List<Hive> hives) {
		BlockPos pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		BiomeGenBase biome = world.getBiomeGenForCoords(pos);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Collections.shuffle(hives, world.rand);
				for (Hive hive : hives) {
					if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
						continue;
					}

					tryGenHive(world, pos.add(x, 0, z), hive);
				}
			}
		}
	}

	private boolean tryGenHive(World world, BlockPos pos, Hive hive) {

		pos = hive.getYForHive(world, pos);

		if (pos.getY() < 0) {
			return false;
		}

		if (!hive.canReplace(world, pos)) {
			return false;
		}

		BiomeGenBase biome = world.getBiomeGenForCoords(pos);
		EnumTemperature temperature = EnumTemperature.getFromValue(biome.getFloatTemperature(pos));
		if (!hive.isGoodTemperature(temperature)) {
			return false;
		}

		if (!hive.isValidLocation(world, pos)) {
			return false;
		}

		return setHive(world, pos, hive);
	}

	protected boolean setHive(World world, BlockPos pos, Hive hive) {
		Block hiveBlock = hive.getHiveBlock();
		boolean placed = world.setBlockState(pos, hiveBlock.getStateFromMeta(hive.getHiveMeta()), Defaults.FLAG_BLOCK_SYNCH);
		if (!placed) {
			return false;
		}

		Block placedBlock = world.getBlockState(pos).getBlock();
		if (!Block.isEqualTo(hiveBlock, placedBlock)) {
			return false;
		}

		hiveBlock.onBlockAdded(world, pos, world.getBlockState(pos));
		world.markBlockForUpdate(pos);

		if (!Config.generateBeehivesDebug) {
			hive.postGen(world, pos);
		}
		return true;
	}
}
