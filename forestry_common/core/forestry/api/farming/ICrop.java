/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.item.ItemStack;

public interface ICrop {

	/**
	 * Harvests this crop. Performs the necessary manipulations to set the crop into a "harvested" state.
	 * 
	 * @return Products harvested.
	 */
	Collection<ItemStack> harvest();

}
