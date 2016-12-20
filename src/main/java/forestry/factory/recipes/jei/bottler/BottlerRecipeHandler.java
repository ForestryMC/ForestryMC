package forestry.factory.recipes.jei.bottler;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BottlerRecipeHandler implements IRecipeHandler<BottlerRecipeWrapper> {

	@Override
	public Class<BottlerRecipeWrapper> getRecipeClass() {
		return BottlerRecipeWrapper.class;
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
		return wrapper.fluid.amount > 0;
	}

}
