package forestry.factory.recipes.jei.carpenter;

import javax.annotation.Nonnull;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CarpenterRecipeHandler implements IRecipeHandler<CarpenterRecipeWrapper> {
	
	@Nonnull
	@Override
	public Class<CarpenterRecipeWrapper> getRecipeClass() {
		return CarpenterRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.CARPENTER;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CarpenterRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CarpenterRecipeWrapper wrapper) {
		ICarpenterRecipe recipe = wrapper.getRecipe();
		if(recipe.getPackagingTime() <= 0){
			return false;
		}
		if(recipe.getCraftingGridRecipe() == null || recipe.getCraftingGridRecipe().getIngredients() == null){
			return false;
		}
		int inputCount = 0;
		for(Object ingredient : recipe.getCraftingGridRecipe().getIngredients()){
			inputCount++;
		}
		return inputCount > 0;
	}

}
