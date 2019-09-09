/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import genetics.api.individual.IIndividual;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.IBlockPosPredicate;

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
	 * Registers an accepted flower with any state.
	 */
	void registerAcceptableFlower(Block block, String... flowerTypes);

	/**
	 * Registers an accepted flower with a particular state.
	 */
	void registerAcceptableFlower(BlockState blockState, String... flowerTypes);

	/**
	 * Registers custom logic for accepted flowers.
	 * These rules are inefficient compared to ones made with registerAcceptableFlower, but may be necessary for tile entity flowers.
	 * The flower types built into Forestry can not be used here.
	 *
	 * @since Forestry 4.0.8
	 */
	void registerAcceptableFlowerRule(IFlowerAcceptableRule flowerAcceptableRule, String... flowerTypes);

	/**
	 * Registers a plantable flower.
	 * The distribution is based on its own weight and the total number of plants for this flowerType.
	 *
	 * @param weight      Weight for the Flower (Vanilla = 1.0, Modded flowers < 1.0)
	 * @param flowerTypes See {@link forestry.api.apiculture.FlowerManager}.FlowerTypeXXX
	 */
	void registerPlantableFlower(BlockState blockState, double weight, String... flowerTypes);

	/**
	 * Calls the appropriate IFlowerGrowthRule to grow a flower at a specified position.
	 *
	 * @since Forestry 5.5.4
	 */
	boolean growFlower(String flowerType, World world, IIndividual individual, BlockPos pos, Collection<BlockState> potentialFlowers);

	/**
	 * Gets an iterator over the area a bee can travel from its beeHousing.
	 * Starts at the bee housing and spirals outward.
	 *
	 * @param beeHousing The bee housing that will help determine the size of the area to check.
	 * @param bee        The bee that will help determine the size of the area to check.
	 * @return an iterator over the area a bee can travel from its beeHousing.
	 * @since Forestry 5.5.2
	 */
	Iterator<BlockPos.MutableBlockPos> getAreaIterator(IBeeHousing beeHousing, IBee bee);

	/**
	 * Checks a single coordinate to see if it is an accepted flower.
	 *
	 * @since Forestry 5.5.2
	 */
	IBlockPosPredicate createAcceptedFlowerPredicate(String flowerType);
}
