/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;

/**
 * Fermenter checks any valid fermentation item for an implementation of this interface.
 * This does not supersede adding a proper recipe to the fermenter!
 */
public interface IVariableFermentable {
	
	/**
	 * @param itemstack
	 * @return Float representing the modification to be applied to the matching recipe's biomass output.
	 */
	float getFermentationModifier(ItemStack itemstack);
}
