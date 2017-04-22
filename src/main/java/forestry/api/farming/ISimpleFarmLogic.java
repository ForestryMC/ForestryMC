/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.item.ItemStack;

/**
 * A simple version of the IFarmLogic. 
 */
public interface ISimpleFarmLogic {

	/**
	 * @return the itemStack that represents this farm logic. Used as an icon for the farm logic.
	 */
	ItemStack getIconItemStack();
	
	Iterable<IFarmable> getSeeds();

	String getName();
	
}
