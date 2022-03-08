/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Unmodifiable;

public interface ICraftingProvider<T extends IForestryRecipe> {

	/**
	 * Add a new recipe to the crafting provider for all worlds.
	 *
	 * @return <tt>true</tt> if this collection changed as a result of the call
	 * @since Forestry 4.1.0
	 */
	boolean addRecipe(T recipe);

	/**
	 * Gets a collection of all currently registered recipes which this provider supports
	 *
	 * @param recipeManager The recipe manager to use
	 * @return A collection of recipes
	 */
	@Unmodifiable
	List<T> getRecipes(@Nullable RecipeManager recipeManager);
}
