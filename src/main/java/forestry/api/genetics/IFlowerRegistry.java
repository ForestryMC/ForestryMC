/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;

// See {@link forestry.api.apiculture.FlowerManager}.FlowerType___ for basic Forestry flower types.
// Each IFlowerProvider has one flower type, and multiple IFlowerProviders may share one type.
// "Acceptable flowers" allow nearby bees to do work.
// "Plantable flowers" are "Acceptable flowers" and are also planted randomly by bees.
public interface IFlowerRegistry {

	/**
	 * Register a new flower growth rule.
	 * This defines the flower planting logic for the specified flower types.
	 * Without a flower growth rule, no flowers of that type can be planted.
	 */
	void registerGrowthRule(IFlowerGrowthRule rule, String... flowerTypes);

	/**
	 * Registers an accepted flower with any meta value.
	 */
	void registerAcceptableFlower(Block flowerBlock, String... flowerTypes);
	/**
	 * Registers an accepted flower with a specific meta value.
	 */
	void registerAcceptableFlower(Block flowerBlock, int flowerMeta, String... flowerTypes);

	/**
	 * Registers custom logic for accepted flowers.
	 * These rules are inefficient compared to ones made with registerAcceptableFlower, but may be necessary for tile entity flowers.
	 * The flower types built into Forestry can not be used here.
	 * @since Forestry 4.0.8
	 */
	void registerAcceptableFlowerRule(IFlowerAcceptableRule flowerAcceptableRule, String... flowerTypes);

	/**
	 * Registers a plantable flower.
	 * The distribution is based on its own weight and the total number of plants for this flowerType.
	 *
	 * @param weight Weight for the Flower (Vanilla = 1.0, Modded flowers < 1.0)
	 * @param flowerTypes See {@link forestry.api.apiculture.FlowerManager}.FlowerTypeXXX
	 */
	void registerPlantableFlower(Block flowerBlock, int flowerMeta, double weight, String... flowerTypes);

	/**
	 * Calls the appropriate IFlowerGrowthRule to grow a flower at a specified position.
	 */
	boolean growFlower(String flowerType, World world, IIndividual individual, int x, int y, int z);

	/**
	 * @return the coordinates of a nearby accepted flower or null if there is none.
	 */
	ChunkCoordinates getAcceptedFlowerCoordinates(IBeeHousing beeHousing, IBee bee, String flowerType);

	/**
	 * Checks a single coordinate to see if it is an accepted flower.
	 */
	boolean isAcceptedFlower(String flowerType, World world, int x, int y, int z);

	/**
	 * For use by IFlowerGrowthRule implementations
	 * @deprecated since Forestry 4.0.8 This will be accessible to IFlowerGrowthRule via IFlowerGrowthHelper
	 */
	@Deprecated
	IFlower getRandomPlantableFlower(String flowerType, Random rand);

	/**
	 * Returns all known flower types.
	 * @deprecated since Forestry 4.0.8 This should only be used internally.
	 */
	@Deprecated
	Collection<String> getFlowerTypes();

	/**
	 * @deprecated since Forestry 4.0.8 This should only be used internally.
	 */
	@Deprecated
	Set<IFlower> getAcceptableFlowers(String flowerType);
}
