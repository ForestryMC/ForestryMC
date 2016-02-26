package forestry.factory.recipes.jei.fabricator;

import javax.annotation.Nonnull;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FabricatorRecipeHandler implements IRecipeHandler<FabricatorRecipeWrapper> {
	
	@Nonnull
	@Override
	public Class<FabricatorRecipeWrapper> getRecipeClass() {
		return FabricatorRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.FABRICATOR;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull FabricatorRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull FabricatorRecipeWrapper wrapper) {
		IFabricatorRecipe recipe = wrapper.getRecipe();
		if (recipe.getIngredients() == null) {
			return false;
		}
		if (recipe.getLiquid() == null) {
			return false;
		}
		if (recipe.getRecipeOutput() == null) {
			return false;
		}
		int inputCount = 0;
		for (Object ingredient : recipe.getIngredients()) {
			inputCount++;
		}
		return inputCount > 0;
	}

}
