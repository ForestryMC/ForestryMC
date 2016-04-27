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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.IPlantable;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.genetics.BeeDefinition;

public enum HiveDescription implements IHiveDescription {

	FOREST(0, 3.0f, BeeDefinition.FOREST, HiveManager.genHelper.tree()) {
		@Override
		public void postGen(World world, BlockPos pos) {
			super.postGen(world, pos);
			postGenFlowers(world, pos, flowerStates);
		}
	},
	MEADOWS(1, 1.0f, BeeDefinition.MEADOWS, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass)) {
		@Override
		public void postGen(World world, BlockPos pos) {
			super.postGen(world, pos);
			postGenFlowers(world, pos, flowerStates);
		}
	},
	DESERT(2, 1.0f, BeeDefinition.MODEST, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass, Blocks.sand, Blocks.sandstone)) {
		@Override
		public void postGen(World world, BlockPos pos) {
			super.postGen(world, pos);
			postGenFlowers(world, pos, cactusStates);
		}
	},
	JUNGLE(3, 6.0f, BeeDefinition.TROPICAL, HiveManager.genHelper.tree()),
	END(4, 6.0f, BeeDefinition.ENDED, HiveManager.genHelper.ground(Blocks.end_stone)) {
		@Override
		public boolean isGoodBiome(BiomeGenBase biome) {
			return BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.END);
		}
	},
	SNOW(5, 2.0f, BeeDefinition.WINTRY, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass, Blocks.snow)) {
		@Override
		public void postGen(World world, BlockPos pos) {
			if (world.isAirBlock(pos.add(0, 1, 0))) {
				world.setBlockState(pos.add(0, 1, 0), Blocks.snow_layer.getStateFromMeta(0), 0);
			}

			postGenFlowers(world, pos, flowerStates);
		}
	},
	SWAMP(6, 2.0f, BeeDefinition.MARSHY, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass)) {
		@Override
		public void postGen(World world, BlockPos pos) {
			super.postGen(world, pos);

			postGenFlowers(world, pos, mushroomStates);
		}
	};

	private static final IHiveGen groundGen = HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass, Blocks.snow, Blocks.sand, Blocks.sandstone);
	private static final List<IBlockState> flowerStates = new ArrayList<>();
	private static final List<IBlockState> mushroomStates = new ArrayList<>();
	private static final List<IBlockState> cactusStates = Collections.singletonList(Blocks.cactus.getDefaultState());

	static {
		flowerStates.addAll(Blocks.red_flower.getBlockState().getValidStates());
		flowerStates.addAll(Blocks.yellow_flower.getBlockState().getValidStates());
		mushroomStates.add(Blocks.red_mushroom.getDefaultState());
		mushroomStates.add(Blocks.brown_mushroom.getDefaultState());
	}

	private final int meta;
	private final float genChance;
	private final IBeeGenome beeGenome;
	private final IHiveGen hiveGen;

	HiveDescription(int meta, float genChance, BeeDefinition beeTemplate, IHiveGen hiveGen) {
		this.meta = meta;
		this.genChance = genChance;
		this.beeGenome = beeTemplate.getGenome();
		this.hiveGen = hiveGen;
	}

	@Override
	public IHiveGen getHiveGen() {
		return hiveGen;
	}

	@Override
	public Block getBlock() {
		return PluginApiculture.blocks.beehives;
	}

	@Override
	public int getMeta() {
		return meta;
	}

	@Override
	public boolean isGoodBiome(BiomeGenBase biome) {
		return !BiomeHelper.isBiomeHellish(biome);
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
	public void postGen(World world, BlockPos pos) {

	}

	protected static void postGenFlowers(World world, BlockPos hivePos, List<IBlockState> flowerStates) {
		int plantedCount = 0;
		for (int i = 0; i < 10; i++) {
			int xOffset = world.rand.nextInt(8) - 4;
			int zOffset = world.rand.nextInt(8) - 4;
			BlockPos blockPos = hivePos.add(xOffset, 0, zOffset);
			if (!world.isBlockLoaded(blockPos) || xOffset == 0 && zOffset == 0) {
				continue;
			}

			int y = groundGen.getYForHive(world, blockPos.getX(), blockPos.getZ());
			if (y <= 0) {
				continue;
			}

			blockPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
			IBlockState state = flowerStates.get(world.rand.nextInt(flowerStates.size()));
			Block block = state.getBlock();
			if (!block.canPlaceBlockAt(world, blockPos)) {
				continue;
			}

			world.setBlockState(blockPos, state);
			plantedCount++;

			if (block instanceof IPlantable) {
				IPlantable plantable = (IPlantable) block;

				BlockPos groundPos = blockPos.down();
				Block ground = world.getBlockState(groundPos).getBlock();
				if (!ground.canSustainPlant(world, groundPos, EnumFacing.UP, plantable)) {
					world.setBlockToAir(blockPos);
					plantedCount--;
				}
			}

			if (plantedCount >= 3) {
				break;
			}
		}
	}
}
