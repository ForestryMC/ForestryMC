/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBackpackDefinition {

	/**
	 * @return A unique string identifier
	 */
	String getKey();

	/**
	 * @return Human-readable name of the backpack.
	 */
	String getName(ItemStack backpack);

	/**
	 * @deprecated since Forestry 3.0. Use getName(ItemStack backpack).
	 */
	@Deprecated
	String getName();

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

	/**
	 * Returns true if the itemstack is a valid item for this backpack type.
	 * @deprecated since Forestry 3.4. Use isValidItem(ItemStack itemstack).
	 */
	@Deprecated
	boolean isValidItem(EntityPlayer player, ItemStack itemstack);

	/**
	 * Returns true if the itemstack is a valid item for this backpack type.
	 */
	boolean isValidItem(ItemStack itemstack);

}
