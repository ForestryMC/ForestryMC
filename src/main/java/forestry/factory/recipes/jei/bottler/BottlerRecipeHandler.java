package forestry.factory.recipes.jei.bottler;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.recipes.BottlerRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BottlerRecipeHandler implements IRecipeHandler<BottlerRecipeWrapper> {

	@Override
	public Class<BottlerRecipeWrapper> getRecipeClass() {
		return BottlerRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Override
	public String getRecipeCategoryUid(BottlerRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(BottlerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(BottlerRecipeWrapper wrapper) {
		BottlerRecipe recipe = wrapper.getRecipe();
		if (recipe.fluid == null || recipe.fluid.amount <= 0) {
			return false;
		}
		if (recipe.inputStack == null) {
			return false;
		}
		return recipe.fluid != null;
	}

}
