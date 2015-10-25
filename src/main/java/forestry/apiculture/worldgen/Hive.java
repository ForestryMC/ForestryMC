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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public final class Hive {

	private final IHiveDescription hiveDescription;
	private final List<IHiveDrop> drops = new ArrayList<IHiveDrop>();

	public Hive(IHiveDescription hiveDescription) {
		if (hiveDescription == null) {
			throw new IllegalArgumentException("Tried to create hive with null hive description");
		}
		this.hiveDescription = hiveDescription;
	}

	public Block getHiveBlock() {
		return hiveDescription.getBlock();
	}

	public int getHiveMeta() {
		return hiveDescription.getMeta();
	}

	public void addDrops(List<IHiveDrop> drops) {
		this.drops.addAll(drops);
	}

	public List<IHiveDrop> getDrops() {
		return drops;
	}

	public float genChance() {
		return hiveDescription.getGenChance();
	}

	public void postGen(World world, BlockPos pos) {
		hiveDescription.postGen(world, pos);
	}

	public boolean isGoodBiome(BiomeGenBase biome) {
		return hiveDescription.isGoodBiome(biome);
	}

	public boolean isGoodHumidity(EnumHumidity humidity) {
		return hiveDescription.isGoodHumidity(humidity);
	}

	public boolean isGoodTemperature(EnumTemperature temperature) {
		return hiveDescription.isGoodTemperature(temperature);
	}

	public boolean isValidLocation(World world, BlockPos pos) {
		return hiveDescription.getHiveGen().isValidLocation(world, pos);
	}

	public boolean canReplace(World world, BlockPos pos) {
		return hiveDescription.getHiveGen().canReplace(world, pos);
	}

	public int getYForHive(World world, BlockPos pos) {
		return hiveDescription.getHiveGen().getYForHive(world, pos);
	}
}
