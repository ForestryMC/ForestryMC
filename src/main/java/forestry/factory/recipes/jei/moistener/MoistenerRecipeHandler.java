package forestry.factory.recipes.jei.moistener;

import javax.annotation.Nonnull;

import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class MoistenerRecipeHandler implements IRecipeHandler<MoistenerRecipeWrapper> {
	
	@Nonnull
	@Override
	public Class<MoistenerRecipeWrapper> getRecipeClass() {
		return MoistenerRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.MOISTENER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(MoistenerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(MoistenerRecipeWrapper wrapper) {
		IMoistenerRecipe recipe = wrapper.getRecipe();
		if(recipe.getTimePerItem() <= 0){
			return false;
		}
		if(recipe.getResource() == null){
			return false;
		}
		if(recipe.getProduct() == null){
			return false;
		}
		return wrapper.getFuel() != null || wrapper.getFuel().item != null|| wrapper.getFuel().product != null;
	}

}
