package forestry.factory.recipes.jei.still;

import javax.annotation.Nonnull;

import forestry.api.recipes.IStillRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class StillRecipeHandler implements IRecipeHandler<StillRecipeWrapper> {
	
	@Nonnull
	@Override
	public Class<StillRecipeWrapper> getRecipeClass() {
		return StillRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.STILL;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(StillRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(StillRecipeWrapper wrapper) {
		IStillRecipe recipe = wrapper.getRecipe();
		if(recipe.getInput() == null){
			return false;
		}
		if(recipe.getOutput() == null){
			return false;
		}
		return true;
	}

}
