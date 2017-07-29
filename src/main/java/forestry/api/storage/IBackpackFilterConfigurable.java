/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A configurable backpack filter. Useful for implementers of {@link IBackpackDefinition}.
 * Get a new instance from {@link IBackpackInterface#createBackpackFilter()}.
 */
public interface IBackpackFilterConfigurable extends Predicate<ItemStack> {
	/**
	 * Adds an item as valid for this backpack to pick up.
	 */
	void acceptItem(ItemStack validItem);

	/**
	 * Adds an item as invalid for this backpack, used to make exceptions to oreDictionary matches.
	 *
	 * @see #acceptOreDictName(String)
	 */
	void rejectItem(ItemStack invalidItem);

	/**
	 * Adds an ore dictionary name as valid for this backpack.
	 * The backpack will pick up any item that has this oreDictName.
	 *
	 * @see OreDictionary
	 */
	void acceptOreDictName(String oreDictName);

	/**
	 * Removes an ore dictionary name as valid for this backpack.
	 * The backpack will not pick up any item that has this oreDictName.
	 *
	 * @see OreDictionary
	 */
	void rejectOreDictName(String oreDictName);

	/**
	 * Clear all the rules from this filter.
	 */
	void clear();
}
