package forestry.factory.recipes.jei.fermenter;

import forestry.api.recipes.IFermenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FermenterRecipeHandler implements IRecipeHandler<FermenterRecipeWrapper> {
	@Override
	public Class<FermenterRecipeWrapper> getRecipeClass() {
		return FermenterRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(FermenterRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.FERMENTER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FermenterRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(FermenterRecipeWrapper wrapper) {
		IFermenterRecipe recipe = wrapper.getRecipe();
		return recipe.getFermentationValue() > 0 &&
				!(recipe.getModifier() <= 0);
	}
}
