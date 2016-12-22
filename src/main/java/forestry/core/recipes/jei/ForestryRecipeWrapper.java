package forestry.core.recipes.jei;

import mezz.jei.api.recipe.BlankRecipeWrapper;

public abstract class ForestryRecipeWrapper<R> extends BlankRecipeWrapper {
	private final R recipe;

	public ForestryRecipeWrapper(R recipe) {
		this.recipe = recipe;
	}

	public R getRecipe() {
		return recipe;
	}
}
