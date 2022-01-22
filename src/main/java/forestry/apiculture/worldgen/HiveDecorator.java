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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.ModuleApiculture;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.utils.Log;

public class HiveDecorator extends Feature<NoneFeatureConfiguration> {
	public HiveDecorator() {
		super(NoneFeatureConfiguration.CODEC);
	}

	private static boolean decorateHivesDebug(WorldGenLevel world, Random rand, BlockPos pos, List<Hive> hives) {
		int posX = pos.getX() + rand.nextInt(16);
		int posZ = pos.getZ() + rand.nextInt(16);

		Biome biome = world.getBiome(new BlockPos(posX, 0, posZ));
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.getDownfall());

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Collections.shuffle(hives, rand);
				for (Hive hive : hives) {
					if (!hive.isGoodBiome(biome) || !hive.isGoodHumidity(humidity)) {
						continue;
					}

					return tryGenHive(world, rand, posX + x, posZ + z, hive);
				}
			}
		}

		return false;
	}

	public static boolean tryGenHive(WorldGenLevel world, Random rand, int x, int z, Hive hive) {
		final BlockPos hivePos = hive.getPosForHive(world, x, z);

		if (hivePos == null) {
			return false;
		}

		if (!hive.canReplace(world, hivePos)) {
			return false;
		}

		Biome biome = world.getBiome(hivePos);
		EnumTemperature temperature = EnumTemperature.NONE; // EnumTemperature.getFromValue(biome.getTemperature(hivePos));
		if (!hive.isGoodTemperature(temperature)) {
			return false;
		}

		if (!hive.isValidLocation(world, hivePos)) {
			return false;
		}

		return setHive(world, rand, hivePos, hive);
	}

	private static boolean setHive(WorldGenLevel world, Random rand, BlockPos pos, Hive hive) {
		BlockState hiveState = hive.getHiveBlockState();
		Block hiveBlock = hiveState.getBlock();
		boolean placed = world.setBlock(pos, hiveState, Constants.FLAG_BLOCK_SYNC);
		if (!placed) {
			return false;
		}

		BlockState state = world.getBlockState(pos);
		Block placedBlock = state.getBlock();
		if (!(hiveBlock == placedBlock)) {
			return false;
		}

		hiveBlock.onPlace(state, world.getLevel(), pos, hiveState, false);

		if (!Config.generateBeehivesDebug) {
			hive.postGen(world, rand, pos);
		}

		if (Config.logHivePlacement) {
			//getCoordinatesAsString
			Log.info("Placed {} at {}", hive.toString(), pos.toShortString());
		}

		return true;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		Random rand = context.random();
		BlockPos pos = context.origin();

		List<Hive> hives = ModuleApiculture.getHiveRegistry().getHives();

		if (Config.generateBeehivesDebug) {
			decorateHivesDebug(level, rand, pos, hives);
			return false;
		}

		Collections.shuffle(hives, rand);

		for (int tries = 0; tries < hives.size() / 2; tries++) {
			Biome biome = level.getBiome(pos);
			EnumHumidity humidity = EnumHumidity.getFromValue(biome.getDownfall());

			for (Hive hive : hives) {
				if (hive.genChance() * Config.generateBeehivesAmount * hives.size() / 8 >= rand.nextFloat() * 100.0f) {
					if (hive.isGoodBiome(biome) && hive.isGoodHumidity(humidity)) {
						int x = pos.getX() + rand.nextInt(16);
						int z = pos.getZ() + rand.nextInt(16);

						if (tryGenHive(level, rand, x, z, hive)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
