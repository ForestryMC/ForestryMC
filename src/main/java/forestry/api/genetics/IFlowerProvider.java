/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IFlowerProvider {

	boolean isAcceptedPollinatable(World world, IPollinatable pollinatable);

	/**
	 * @return The unique type used for the IFlowerRegistry
	 * @since Forestry 4.0.0
	 */
	String getFlowerType();

	/**
	 * @return Short, human-readable identifier used in the beealyzer.
	 */
	String getDescription();

	/**
	 * Allows the flower provider to affect the produce at the given location.
	 * If this flowerProvider does not affect the products, it should return the products unchanged.
	 * @return Array of itemstacks being the (modified or unmodified) produce.
	 */
	ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products);

	/**
	 * @return Set of valid flowers for the flower provider.
	 *  Returns an empty set if the flower provider does not have any valid flowers.
	 * @deprecated since Forestry 4.0.8 Use more specific methods in IFlowerRegistry.
	 */
	@Deprecated
	Set<IFlower> getFlowers();

	/**
	 * @return True if a flower was planted.
	 * @deprecated since Forestry 4.0.8 Use IFlowerRegistry.growFlower.
	 * Implementers can move logic into a IFlowerGrowthRule and register with IFlowerRegistry.registerGrowthRule
	 */
	@Deprecated
	boolean growFlower(World world, IIndividual individual, int x, int y, int z);
}
