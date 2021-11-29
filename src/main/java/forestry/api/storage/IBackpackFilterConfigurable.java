/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 * A configurable backpack filter. Useful for implementers of {@link IBackpackDefinition}.
 * Get a new instance from {@link IBackpackInterface#createBackpackFilter()}.
 */
public interface IBackpackFilterConfigurable extends Predicate<ItemStack> {

	/**
	 * Adds an item or tag as valid for this filter
	 *
	 * @param ingredient The items or tags to allow
	 */
	void accept(Ingredient ingredient);

	/**
	 * Adds an item or tag as invalid for this filter
	 *
	 * @param ingredient The items or tags to reject
	 */
	void reject(Ingredient ingredient);

	/**
	 * Clear all the rules from this filter.
	 */
	void clear();
}
