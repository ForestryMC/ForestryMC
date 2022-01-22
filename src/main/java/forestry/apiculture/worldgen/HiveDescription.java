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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.core.config.Constants;

import genetics.api.individual.IGenome;

public enum HiveDescription implements IHiveDescription {
	FOREST(IHiveRegistry.HiveType.FOREST, 3.0f, BeeDefinition.FOREST, HiveManager.genHelper.tree()) {
		@Override
		public void postGen(WorldGenLevel world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);
			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	MEADOWS(IHiveRegistry.HiveType.MEADOWS, 1.0f, BeeDefinition.MEADOWS, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS_BLOCK)) {
		@Override
		public void postGen(WorldGenLevel world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);
			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	DESERT(IHiveRegistry.HiveType.DESERT, 1.0f, BeeDefinition.MODEST, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.SANDSTONE)) {
		@Override
		public void postGen(WorldGenLevel world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);
			postGenFlowers(world, rand, pos, cactusStates);
		}
	},
	JUNGLE(IHiveRegistry.HiveType.JUNGLE, 6.0f, BeeDefinition.TROPICAL, HiveManager.genHelper.tree()),
	END(IHiveRegistry.HiveType.END, 2.0f, BeeDefinition.ENDED, HiveManager.genHelper.ground(Blocks.END_STONE, Blocks.END_STONE_BRICKS)) {
		@Override
		public boolean isGoodBiome(Biome biome) {
			return biome.getBiomeCategory() == Biome.BiomeCategory.THEEND;
		}
	},
	SNOW(IHiveRegistry.HiveType.SNOW, 2.0f, BeeDefinition.WINTRY, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.SNOW, Blocks.GRASS_BLOCK)) {
		@Override
		public void postGen(WorldGenLevel world, Random rand, BlockPos pos) {
			BlockPos posAbove = pos.above();
			if (world.isEmptyBlock(posAbove)) {
				world.setBlock(posAbove, Blocks.SNOW.defaultBlockState(), Constants.FLAG_BLOCK_SYNC);
			}

			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	SWAMP(IHiveRegistry.HiveType.SWAMP, 2.0f, BeeDefinition.MARSHY, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS_BLOCK)) {
		@Override
		public void postGen(WorldGenLevel world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);

			postGenFlowers(world, rand, pos, mushroomStates);
		}
	};

	private static final IHiveGen groundGen = HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.SNOW, Blocks.SAND, Blocks.SANDSTONE);
	private static final List<BlockState> flowerStates = new ArrayList<>();
	private static final List<BlockState> mushroomStates = new ArrayList<>();
	private static final List<BlockState> cactusStates = Collections.singletonList(Blocks.CACTUS.defaultBlockState());

	static {
		flowerStates.addAll(Blocks.POPPY.getStateDefinition().getPossibleStates());
		flowerStates.addAll(Blocks.DANDELION.getStateDefinition().getPossibleStates());
		mushroomStates.add(Blocks.RED_MUSHROOM.defaultBlockState());
		mushroomStates.add(Blocks.BROWN_MUSHROOM.defaultBlockState());
	}

	private final BlockState blockState;
	private final float genChance;
	private final IGenome beeGenome;
	private final IHiveGen hiveGen;
	private final IHiveRegistry.HiveType hiveType;

	HiveDescription(IHiveRegistry.HiveType hiveType, float genChance, BeeDefinition beeTemplate, IHiveGen hiveGen) {
		this.blockState = ApicultureBlocks.BEEHIVE.get(hiveType).defaultState();
		this.genChance = genChance;
		this.beeGenome = beeTemplate.getGenome();
		this.hiveGen = hiveGen;
		this.hiveType = hiveType;
	}

	@Override
	public IHiveGen getHiveGen() {
		return hiveGen;
	}

	@Override
	public BlockState getBlockState() {
		return blockState;
	}

	@Override
	public boolean isGoodBiome(Biome biome) {
		return !BiomeHelper.isBiomeHellish(biome);
	}

	@Override
	public boolean isGoodHumidity(EnumHumidity humidity) {
		EnumHumidity idealHumidity = beeGenome.getActiveAllele(BeeChromosomes.SPECIES).getHumidity();
		EnumTolerance humidityTolerance = beeGenome.getActiveValue(BeeChromosomes.HUMIDITY_TOLERANCE);
		return AlleleManager.climateHelper.isWithinLimits(humidity, idealHumidity, humidityTolerance);
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		EnumTemperature idealTemperature = beeGenome.getActiveAllele(BeeChromosomes.SPECIES).getTemperature();
		EnumTolerance temperatureTolerance = beeGenome.getActiveValue(BeeChromosomes.TEMPERATURE_TOLERANCE);
		return AlleleManager.climateHelper.isWithinLimits(temperature, idealTemperature, temperatureTolerance);
	}

	@Override
	public float getGenChance() {
		return genChance;
	}

	@Override
	public void postGen(WorldGenLevel world, Random rand, BlockPos pos) {
	}

	protected static void postGenFlowers(WorldGenLevel world, Random rand, BlockPos hivePos, List<BlockState> flowerStates) {
		int plantedCount = 0;
		for (int i = 0; i < 10; i++) {
			int xOffset = rand.nextInt(8) - 4;
			int zOffset = rand.nextInt(8) - 4;
			BlockPos blockPos = hivePos.offset(xOffset, 0, zOffset);
			if ((xOffset == 0 && zOffset == 0) || !world.hasChunkAt(blockPos)) {
				continue;
			}

			blockPos = groundGen.getPosForHive(world, blockPos.getX(), blockPos.getZ());
			if (blockPos == null) {
				continue;
			}

			BlockState state = flowerStates.get(rand.nextInt(flowerStates.size()));
			Block block = state.getBlock();
			if (!block.defaultBlockState().canSurvive(world, blockPos)) {
				continue;
			}

			world.setBlock(blockPos, state, Constants.FLAG_BLOCK_SYNC);
			plantedCount++;

			if (plantedCount >= 3) {
				break;
			}
		}
	}
}
