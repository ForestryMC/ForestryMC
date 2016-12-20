package forestry.factory.recipes.jei.moistener;

import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class MoistenerRecipeHandler implements IRecipeHandler<MoistenerRecipeWrapper> {
	@Override
	public Class<MoistenerRecipeWrapper> getRecipeClass() {
		return MoistenerRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid( MoistenerRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.MOISTENER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper( MoistenerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid( MoistenerRecipeWrapper wrapper) {
		IMoistenerRecipe recipe = wrapper.getRecipe();
		return recipe.getTimePerItem() > 0;
	}
}
