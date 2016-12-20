package forestry.factory.recipes.jei.fabricator;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FabricatorRecipeHandler implements IRecipeHandler<FabricatorRecipeWrapper> {
	@Override
	public Class<FabricatorRecipeWrapper> getRecipeClass() {
		return FabricatorRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(FabricatorRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.FABRICATOR;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FabricatorRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(FabricatorRecipeWrapper wrapper) {
		IFabricatorRecipe recipe = wrapper.getRecipe();
		int inputCount = recipe.getIngredients().size();
		return inputCount > 0;
	}

}
