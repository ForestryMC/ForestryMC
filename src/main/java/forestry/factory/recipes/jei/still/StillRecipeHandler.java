package forestry.factory.recipes.jei.still;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class StillRecipeHandler implements IRecipeHandler<StillRecipeWrapper> {
	@Override
	public Class<StillRecipeWrapper> getRecipeClass() {
		return StillRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(StillRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.STILL;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(StillRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(StillRecipeWrapper wrapper) {
		return true;
	}
}
