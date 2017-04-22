/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

/**
 * To make your own backpack, create a backpack definition and register it with
 * {@link IBackpackInterface#registerBackpackDefinition(String, IBackpackDefinition)}.
 */
public interface IBackpackDefinition {
	/**
	 * @return Human-readable name of the backpack.
	 */
	String getName(ItemStack backpack);

	/**
	 * @return Primary color for the backpack icon.
	 */
	int getPrimaryColour();

	/**
	 * @return Secondary color for backpack icon, normally white.
	 */
	int getSecondaryColour();

	/**
	 * Filters items that can be put into a backpack.
	 * <p>
	 * For Backpack Implementers: you can create a new filter with
	 * {@link IBackpackInterface#createBackpackFilter()} or
	 * {@link IBackpackInterface#createNaturalistBackpackFilter(String)}
	 * or implement your own.
	 *
	 * @return the backpack's item filter.
	 */
	Predicate<ItemStack> getFilter();
}
