package forestry.factory.recipes.jei.centrifuge;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CentrifugeRecipeHandler implements IRecipeHandler<CentrifugeRecipeWrapper> {

	@Override
	public Class<CentrifugeRecipeWrapper> getRecipeClass() {
		return CentrifugeRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(CentrifugeRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CentrifugeRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(CentrifugeRecipeWrapper wrapper) {
		ICentrifugeRecipe recipe = wrapper.getRecipe();
		if (recipe.getProcessingTime() <= 0) {
			return false;
		}
		if (recipe.getInput().isEmpty()) {
			return false;
		}
		int inputCount = recipe.getAllProducts().keySet().size();
		return inputCount > 0;
	}

}
