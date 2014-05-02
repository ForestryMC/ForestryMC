/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

/**
 * Any housing, hatchery or nest which is a fixed location in the world. 
 */
public interface IHousing {

	/**
	 * @return String containing the login of this housing's owner.
	 */
	String getOwnerName();

	World getWorld();

	int getXCoord();

	int getYCoord();

	int getZCoord();

	int getBiomeId();

	EnumTemperature getTemperature();

	EnumHumidity getHumidity();

	void setErrorState(int state);

	int getErrorOrdinal();

	/**
	 * Adds products to the housing's inventory.
	 * 
	 * @param product
	 *            ItemStack with the product to add.
	 * @param all
	 * @return Boolean indicating success or failure.
	 */
	boolean addProduct(ItemStack product, boolean all);

}
