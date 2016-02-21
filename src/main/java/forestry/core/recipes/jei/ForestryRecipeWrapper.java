package forestry.core.recipes.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.BlankRecipeWrapper;

public abstract class ForestryRecipeWrapper<R> extends BlankRecipeWrapper {
	
	@Nonnull
	private final R recipe;
	
	public ForestryRecipeWrapper(@Nonnull R recipe) {
		this.recipe = recipe;
	}

	@Nonnull
	public R getRecipe() {
		return recipe;
	}

}
