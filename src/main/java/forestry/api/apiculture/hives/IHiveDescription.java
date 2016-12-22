/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import java.util.Random;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IHiveDescription {

	/**
	 * The hive generator for this hive.
	 */
	IHiveGen getHiveGen();

	/**
	 * The hive block to be placed in the world.
	 */
	IBlockState getBlockState();

	/**
	 * returns true if the hive can be generated in these conditions.
	 * Used as a fast early-elimination check for hives that have no hope of spawning in the area.
	 */
	boolean isGoodBiome(Biome biome);

	boolean isGoodHumidity(EnumHumidity humidity);

	boolean isGoodTemperature(EnumTemperature temperature);

	/**
	 * float representing the relative chance a hive will generate in a chunk.
	 * Default is 1.0, higher numbers result in more hives, smaller will result in fewer.
	 * Tree hives want around 3.0 to 4.0 since there are less locations to generate on.
	 */
	float getGenChance();

	/**
	 * Called after successful hive generation.
	 * world, x, y, z give the location of the new hive.
	 **/
	void postGen(World world, Random rand, BlockPos pos);
}
