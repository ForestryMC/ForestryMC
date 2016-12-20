package forestry.factory.recipes.jei.rainmaker;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RainmakerRecipeHandler implements IRecipeHandler<RainmakerRecipeWrapper> {
	@Override
	public Class<RainmakerRecipeWrapper> getRecipeClass() {
		return RainmakerRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(RainmakerRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.RAINMAKER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(RainmakerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(RainmakerRecipeWrapper wrapper) {
		return true;
	}
}
