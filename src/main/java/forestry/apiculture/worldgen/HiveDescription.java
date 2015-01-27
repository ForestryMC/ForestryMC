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

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.ForestryBlock;

public enum HiveDescription implements IHiveDescription {

	FOREST(1, 3.0f, EnumSet.of(EnumHumidity.NORMAL), EnumSet.of(EnumTemperature.NORMAL), HiveManager.genHelper.tree()),
	MEADOWS(2, 1.0f, EnumSet.of(EnumHumidity.NORMAL), EnumSet.of(EnumTemperature.NORMAL), HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass)),
	DESERT(3, 1.0f, EnumSet.of(EnumHumidity.ARID), EnumSet.of(EnumTemperature.HOT), HiveManager.genHelper.ground(Blocks.sand, Blocks.sandstone)),
	JUNGLE(4, 4.0f, EnumSet.of(EnumHumidity.DAMP), EnumSet.of(EnumTemperature.WARM), HiveManager.genHelper.tree()),
	END(5, 4.0f, EnumSet.allOf(EnumHumidity.class), EnumSet.allOf(EnumTemperature.class), HiveManager.genHelper.ground(Blocks.end_stone)) {
		@Override
		public boolean isGoodBiome(BiomeGenBase biome) {
			return biome.biomeID == BiomeGenBase.sky.biomeID;
		}
	},
	SNOW(6, 2.0f, EnumSet.allOf(EnumHumidity.class), EnumSet.of(EnumTemperature.COLD, EnumTemperature.ICY), HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass, Blocks.snow)) {
		@Override
		public void postGen(World world, int x, int y, int z) {
			if (world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 0);
			}
		}
	},
	SWAMP(7, 2.0f, EnumSet.of(EnumHumidity.DAMP), EnumSet.of(EnumTemperature.NORMAL), HiveManager.genHelper.ground(Blocks.dirt, Blocks.grass)),;

	private final int meta;
	private final float genChance;
	private final EnumSet<EnumHumidity> humidities;
	private final EnumSet<EnumTemperature> temperatures;
	private final IHiveGen hiveGen;

	private HiveDescription(int meta, float genChance, EnumSet<EnumHumidity> humidities, EnumSet<EnumTemperature> temperatures, IHiveGen hiveGen) {
		this.meta = meta;
		this.genChance = genChance;
		this.humidities = humidities;
		this.temperatures = temperatures;
		this.hiveGen = hiveGen;
	}

	@Override
	public IHiveGen getHiveGen() {
		return hiveGen;
	}

	@Override
	public Block getBlock() {
		return ForestryBlock.beehives.block();
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
		return humidities.contains(humidity);
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		return temperatures.contains(temperature);
	}

	@Override
	public float getGenChance() {
		return genChance;
	}

	@Override
	public void postGen(World world, int x, int y, int z) {

	}
}
