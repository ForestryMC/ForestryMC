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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.ModuleApiculture;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.utils.Log;

public abstract class HiveDecorator {

	@Nullable
	private static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "FORESTRY_HIVES", new Class[0]);

	public static void decorateHives(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (!TerrainGen.populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, EVENT_TYPE)) {
			return;
		}

		decorateHives(world, rand, chunkX, chunkZ);
	}

	public static void decorateHives(World world, Random rand, int chunkX, int chunkZ) {
		List<Hive> hives = ModuleApiculture.getHiveRegistry().getHives();

		if (Config.generateBeehivesDebug) {
			decorateHivesDebug(world, rand, chunkX, chunkZ, hives);
			return;
		}

		int worldX = (chunkX << 4) + 8;
		int worldZ = (chunkZ << 4) + 8;

		Collections.shuffle(hives, rand);

		for (int tries = 0; tries < hives.size() / 2; tries++) {
			int x = worldX + rand.nextInt(16);
			int z = worldZ + rand.nextInt(16);

			BlockPos pos = new BlockPos(x, 0, z);
			if (!world.isBlockLoaded(pos)) {
				Log.error("tried to generate a hive in an unloaded area.");
				return;
			}
			Biome biome = world.getBiome(pos);
			EnumHumidity humidity = EnumHumidity.getFromValue(biome.getRainfall());

			for (Hive hive : hives) {
				if (hive.genChance() * Config.getBeehivesAmount() * hives.size() / 8 >= rand.nextFloat() * 100.0f) {
					if (hive.isGoodBiome(biome) && hive.isGoodHumidity(humidity)) {
						if (tryGenHive(world, rand, x, z, hive)) {
							return;
						}
					}
				}
			}
		}
	}

	private static void decorateHivesDebug(World world, Random rand, int chunkX, int chunkZ, List<Hive> hives) {
		int worldX = (chunkX << 4) + 8;
		int worldZ = (chunkZ << 4) + 8;
		Biome biome = world.getBiome(new BlockPos(chunkX, 0, chunkZ));
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.getRainfall());

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Collections.shuffle(hives, world.rand);
				for (Hive hive : hives) {
					if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
						continue;
					}

					tryGenHive(world, rand, worldX + x, worldZ + z, hive);
				}
			}
		}
	}

	public static boolean tryGenHive(World world, Random rand, int x, int z, Hive hive) {

		final BlockPos hivePos = hive.getPosForHive(world, x, z);

		if (hivePos == null) {
			return false;
		}

		if (!hive.canReplace(world, hivePos)) {
			return false;
		}

		Biome biome = world.getBiome(hivePos);
		EnumTemperature temperature = EnumTemperature.getFromValue(biome.getTemperature(hivePos));
		if (!hive.isGoodTemperature(temperature)) {
			return false;
		}

		if (!hive.isValidLocation(world, hivePos)) {
			return false;
		}

		return setHive(world, rand, hivePos, hive);
	}

	private static boolean setHive(World world, Random rand, BlockPos pos, Hive hive) {
		IBlockState hiveState = hive.getHiveBlockState();
		Block hiveBlock = hiveState.getBlock();
		boolean placed = world.setBlockState(pos, hiveState, Constants.FLAG_BLOCK_SYNC);
		if (!placed) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		Block placedBlock = state.getBlock();
		if (!Block.isEqualTo(hiveBlock, placedBlock)) {
			return false;
		}

		hiveBlock.onBlockAdded(world, pos, state);

		if (!Config.generateBeehivesDebug) {
			hive.postGen(world, rand, pos);
		}

		if (Config.logHivePlacement) {
			Log.info("Placed {} at {}", hive, pos);
		}

		return true;
	}
}
