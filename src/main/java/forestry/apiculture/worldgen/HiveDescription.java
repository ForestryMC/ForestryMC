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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.plugins.PluginApiculture;

public enum HiveDescription implements IHiveDescription {

	FOREST(1, 3.0f, BeeDefinition.FOREST, HiveManager.genHelper.tree()),
	MEADOWS(2, 1.0f, BeeDefinition.MEADOWS, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass)),
	DESERT(3, 1.0f, BeeDefinition.MODEST, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass, Blocks.sand, Blocks.sandstone)),
	JUNGLE(4, 4.0f, BeeDefinition.TROPICAL, HiveManager.genHelper.tree()),
	END(5, 4.0f, BeeDefinition.ENDED, HiveManager.genHelper.ground(Blocks.end_stone)) {
		@Override
		public boolean isGoodBiome(BiomeGenBase biome) {
			return BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.END);
		}
	},
	SNOW(6, 2.0f, BeeDefinition.WINTRY, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass, Blocks.snow)) {
		@Override
		public void postGen(World world, int x, int y, int z) {
			if (world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 0);
			}
		}
	},
	SWAMP(7, 2.0f, BeeDefinition.MARSHY, HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass)),;

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
	public void postGen(World world, int x, int y, int z) {

	}
}
