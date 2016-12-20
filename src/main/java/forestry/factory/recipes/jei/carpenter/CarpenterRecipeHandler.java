package forestry.factory.recipes.jei.carpenter;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CarpenterRecipeHandler implements IRecipeHandler<CarpenterRecipeWrapper> {

	@Override
	public Class<CarpenterRecipeWrapper> getRecipeClass() {
		return CarpenterRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid( CarpenterRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.CARPENTER;
	}
	
	@Override
	public IRecipeWrapper getRecipeWrapper( CarpenterRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid( CarpenterRecipeWrapper wrapper) {
		ICarpenterRecipe recipe = wrapper.getRecipe();
		if (recipe.getPackagingTime() <= 0) {
			return false;
		}
		int inputCount = recipe.getCraftingGridRecipe().getIngredients().size();
		return inputCount > 0;
	}

}
