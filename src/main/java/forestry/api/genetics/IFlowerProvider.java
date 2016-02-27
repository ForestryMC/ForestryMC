/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IFlowerProvider {

	boolean isAcceptedPollinatable(@Nonnull World world, @Nonnull IPollinatable pollinatable);

	/**
	 * @return The unique type used for the IFlowerRegistry
	 * @since Forestry 4.0.0
	 */
	@Nonnull
	String getFlowerType();

	/**
	 * @return Short, human-readable identifier used in the beealyzer.
	 */
	@Nonnull
	String getDescription();

	/**
	 * Allows the flower provider to affect the produce at the given location.
	 * If this flowerProvider does not affect the products, it should return the products unchanged.
	 * @return Array of itemstacks being the (modified or unmodified) produce.
	 */
	@Nonnull
	ItemStack[] affectProducts(World world, IIndividual individual, BlockPos pos, ItemStack[] products);
}
