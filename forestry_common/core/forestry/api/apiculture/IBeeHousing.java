/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IHousing;

public interface IBeeHousing extends IBeeModifier, IBeeListener, IHousing {

	ItemStack getQueen();

	ItemStack getDrone();

	void setQueen(ItemStack itemstack);

	void setDrone(ItemStack itemstack);

	/**
	 * @return true if princesses and drones can (currently) mate in this housing to generate queens.
	 */
	boolean canBreed();

}
