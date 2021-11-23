/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import java.util.Collection;
import java.util.Map;

public interface ICraftingProvider<T extends IForestryRecipe> {
	/**
	 * Add a new recipe to the crafting provider.
	 * @return <tt>true</tt> if this collection changed as a result of the call
	 * @since Forestry 4.1.0
	 */
	boolean addRecipe(T recipe);

	/**
	 * Remove a specific recipe from the crafting provider.
	 * @return <tt>true</tt> if an element was removed as a result of this call
	 * @since Forestry 4.1.0
	 */
	boolean removeRecipe(T recipe);

	/**
	 * @return an unmodifiable collection of all recipes registered to the crafting provider.
	 * @since Forestry 4.1.0
	 */
	Collection<T> recipes();

	/**
	 * Access to the full list of recipes contained in the crafting provider.
	 *
	 * @return List of the given format where the first array represents inputs and the second outputs. Objects can be either ItemStack or LiquidStack.
	 * @deprecated since Forestry 4.1.0. Use recipes()
	 */
	@Deprecated
	Map<Object[], Object[]> getRecipes();
}
