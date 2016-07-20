/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import javax.annotation.Nonnull;
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
	@Nonnull
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
	 *
	 * For Backpack Implementers: you can create a new filter with
	 * {@link IBackpackInterface#createBackpackFilter()} or
	 * {@link IBackpackInterface#createNaturalistBackpackFilter(String)}
	 * or implement your own.
	 *
	 * @return the backpack's item filter.
	 */
	@Nonnull
	Predicate<ItemStack> getFilter();
}
