/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.item.ItemStack;

public interface IBeeHousingInventory {

	ItemStack getQueen();
	ItemStack getDrone();

	void setQueen(ItemStack itemstack);
	void setDrone(ItemStack itemstack);

	/**
	 * Adds products to the housing's inventory.
	 *
	 * @param product ItemStack with the product to add.
	 * @param all if true, success requires that all products are added
	 * @return boolean indicating success or failure.
	 */
	boolean addProduct(ItemStack product, boolean all);
}
