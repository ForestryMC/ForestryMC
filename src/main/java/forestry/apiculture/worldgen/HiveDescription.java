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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.apiculture.HiveConfig;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.core.config.Constants;

public enum HiveDescription implements IHiveDescription {

	FOREST(IHiveRegistry.HiveType.FOREST, 3.0f, BeeDefinition.FOREST, HiveManager.genHelper.tree()) {
		@Override
		public void postGen(World world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);
			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	MEADOWS(IHiveRegistry.HiveType.MEADOWS, 1.0f, BeeDefinition.MEADOWS, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS)) {
		@Override
		public void postGen(World world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);
			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	DESERT(IHiveRegistry.HiveType.DESERT, 1.0f, BeeDefinition.MODEST, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS, Blocks.SAND, Blocks.SANDSTONE)) {
		@Override
		public void postGen(World world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);
			postGenFlowers(world, rand, pos, cactusStates);
		}
	},
	JUNGLE(IHiveRegistry.HiveType.JUNGLE, 6.0f, BeeDefinition.TROPICAL, HiveManager.genHelper.tree()),
	END(IHiveRegistry.HiveType.END, 2.0f, BeeDefinition.ENDED, HiveManager.genHelper.ground(Blocks.END_STONE, Blocks.END_BRICKS)) {
		@Override
		public boolean isGoodBiome(Biome biome) {
			return BiomeDictionary.hasType(biome, BiomeDictionary.Type.END);
		}
	},
	SNOW(IHiveRegistry.HiveType.SNOW, 2.0f, BeeDefinition.WINTRY, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS, Blocks.SNOW)) {
		@Override
		public void postGen(World world, Random rand, BlockPos pos) {
			BlockPos posAbove = pos.up();
			if (world.isAirBlock(posAbove)) {
				world.setBlockState(posAbove, Blocks.SNOW_LAYER.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
			}

			postGenFlowers(world, rand, pos, flowerStates);
		}
	},
	SWAMP(IHiveRegistry.HiveType.SWAMP, 2.0f, BeeDefinition.MARSHY, HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS)) {
		@Override
		public void postGen(World world, Random rand, BlockPos pos) {
			super.postGen(world, rand, pos);

			postGenFlowers(world, rand, pos, mushroomStates);
		}
	};

	private static final IHiveGen groundGen = HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS, Blocks.SNOW, Blocks.SAND, Blocks.SANDSTONE);
	private static final List<IBlockState> flowerStates = new ArrayList<>();
	private static final List<IBlockState> mushroomStates = new ArrayList<>();
	private static final List<IBlockState> cactusStates = Collections.singletonList(Blocks.CACTUS.getDefaultState());

	static {
		flowerStates.addAll(Blocks.RED_FLOWER.getBlockState().getValidStates());
		flowerStates.addAll(Blocks.YELLOW_FLOWER.getBlockState().getValidStates());
		mushroomStates.add(Blocks.RED_MUSHROOM.getDefaultState());
		mushroomStates.add(Blocks.BROWN_MUSHROOM.getDefaultState());
	}

	private final IBlockState blockState;
	private final float genChance;
	private final IBeeGenome beeGenome;
	private final IHiveGen hiveGen;
	private final IHiveRegistry.HiveType hiveType;

	HiveDescription(IHiveRegistry.HiveType hiveType, float genChance, BeeDefinition beeTemplate, IHiveGen hiveGen) {
		this.blockState = ModuleApiculture.getBlocks().beehives.getStateForType(hiveType);
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
	public IBlockState getBlockState() {
		return blockState;
	}

	@Override
	public boolean isGoodBiome(Biome biome) {
		return !BiomeHelper.isBiomeHellish(biome) && !HiveConfig.isBlacklisted(hiveType, biome);
	}

	@Override
	public boolean isGoodHumidity(EnumHumidity humidity) {
		EnumHumidity idealHumidity = beeGenome.getPrimary().getHumidity();
		EnumTolerance humidityTolerance = beeGenome.getToleranceHumid();
		return AlleleManager.climateHelper.isWithinLimits(humidity, idealHumidity, humidityTolerance);
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		EnumTemperature idealTemperature = beeGenome.getPrimary().getTemperature();
		EnumTolerance temperatureTolerance = beeGenome.getToleranceTemp();
		return AlleleManager.climateHelper.isWithinLimits(temperature, idealTemperature, temperatureTolerance);
	}

	@Override
	public float getGenChance() {
		return genChance;
	}

	@Override
	public void postGen(World world, Random rand, BlockPos pos) {
	}

	protected static void postGenFlowers(World world, Random rand, BlockPos hivePos, List<IBlockState> flowerStates) {
		int plantedCount = 0;
		for (int i = 0; i < 10; i++) {
			int xOffset = rand.nextInt(8) - 4;
			int zOffset = rand.nextInt(8) - 4;
			BlockPos blockPos = hivePos.add(xOffset, 0, zOffset);
			if ((xOffset == 0 && zOffset == 0) || !world.isBlockLoaded(blockPos)) {
				continue;
			}

			blockPos = groundGen.getPosForHive(world, blockPos.getX(), blockPos.getZ());
			if (blockPos == null) {
				continue;
			}

			IBlockState state = flowerStates.get(rand.nextInt(flowerStates.size()));
			Block block = state.getBlock();
			if (!block.canPlaceBlockAt(world, blockPos)) {
				continue;
			}

			world.setBlockState(blockPos, state);
			plantedCount++;

			if (plantedCount >= 3) {
				break;
			}
		}
	}
}
