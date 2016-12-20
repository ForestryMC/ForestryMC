package forestry.factory.recipes.jei.squeezer;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SqueezerRecipeHandler<R extends AbstractSqueezerRecipeWrapper> implements IRecipeHandler<R> {
	private final Class<R> recipeClass;

	public SqueezerRecipeHandler(Class<R> recipeClass) {
		this.recipeClass = recipeClass;
	}

	@Override
	public Class<R> getRecipeClass() {
		return recipeClass;
	}

	@Override
	public String getRecipeCategoryUid(R recipe) {
		return ForestryRecipeCategoryUid.SQUEEZER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(R recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(R wrapper) {
		return true;
	}

}
