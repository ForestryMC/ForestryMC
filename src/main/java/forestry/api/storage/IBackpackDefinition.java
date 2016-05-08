/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

public interface IBackpackDefinition extends Predicate<ItemStack> {

	/**
	 * @return Human-readable name of the backpack.
	 */
	String getName(ItemStack backpack);

	/**
	 * @return Primary colour for the backpack icon.
	 */
	int getPrimaryColour();

	/**
	 * @return Secondary colour for backpack icon.
	 */
	int getSecondaryColour();

	/**
	 * Adds an item as valid for this backpack.
	 *
	 * @param validItem
	 */
	void addValidItem(ItemStack validItem);

	void addValidItems(List<ItemStack> validItems);

	void addValidOreDictName(String oreDictName);

	/**
	 * Returns true if the ItemStack is a valid item for this backpack type.
	 */
	@Override
	boolean test(ItemStack itemstack);

}
