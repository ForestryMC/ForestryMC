/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Helper interface that every farm inventory implements.
 * <p>
 * Handles the inventory and item management of the farm. Which item the farm will accept is defined by the
 * {@link IFarmLogic}'s and the {@link IFarmProperties}.
 */
public interface IFarmInventory {
	/**
	 * Checks if the inventory contains the given resources.
	 *
	 * @param resources A list of items to check
	 * @return True if the inventory contains the resources; false otherwise.
	 */
	boolean hasResources(NonNullList<ItemStack> resources);

	/**
	 * Remove the given resource from the inventory, if possible
	 *
	 * @param resources A list of items to remove
	 */
	void removeResources(NonNullList<ItemStack> resources);

	/**
	 * Checks if the inventory will accept the given stack as a sapling
	 *
	 * @param stack The stack to check
	 * @return True if the stack is valid; false otherwise
	 */
	boolean acceptsAsSeedling(ItemStack stack);

	/**
	 * Checks if the inventory will accept the given stack as a resource
	 *
	 * @param stack The stack to check
	 * @return True if the stack is valid; false otherwise
	 */
	boolean acceptsAsResource(ItemStack stack);

	/**
	 * Checks if the inventory will accept the given stack as a fertelizer
	 *
	 * @param stack The stack to check
	 * @return True if the stack is valid; false otherwise
	 */
	boolean acceptsAsFertilizer(ItemStack stack);

	/**
	 * Sub-inventory that contains only the output
	 */
	IInventory getProductInventory();

	/**
	 * Sub-inventory that contains only the germlings
	 */
	IInventory getGermlingsInventory();

	/**
	 * Sub-inventory that contains only the resources
	 */
	IInventory getResourcesInventory();

	/**
	 * Sub-inventory that contains only the fertilizer
	 */
	IInventory getFertilizerInventory();
}
