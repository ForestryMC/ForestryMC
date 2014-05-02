/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.recipes;

import java.util.Map;

public interface ICraftingProvider {
	/**
	 * Access to the full list of recipes contained in the crafting provider.
	 * 
	 * @return List of the given format where the first array represents inputs and the second outputs. Objects can be either ItemStack or LiquidStack.
	 */
	public Map<Object[], Object[]> getRecipes();
}
