package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeWrapper;

public abstract class AbstractSqueezerRecipeWrapper<R> extends ForestryRecipeWrapper<R> {
	public AbstractSqueezerRecipeWrapper(@Nonnull R recipe) {
		super(recipe);
	}

	/**
	 * @return Chance remnants will be produced by a single recipe cycle, from 0 to 1.
	 */
	public abstract float getRemnantsChance();
}
