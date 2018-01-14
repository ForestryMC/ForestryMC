/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.item.ItemStack;

/**
 * A simple version of the IFarmLogic.
 *
 * @apiNote TODO Remove this in 1.13
 */
public interface ISimpleFarmLogic {

	/**
	 * @return the itemStack that represents this farm logic. Used as an icon for the farm logic.
	 */
	ItemStack getIconItemStack();

	/**
	 * @deprecated Since 5.8
	 */
	@Deprecated
	Iterable<IFarmable> getSeeds();

	String getName();
	
}
