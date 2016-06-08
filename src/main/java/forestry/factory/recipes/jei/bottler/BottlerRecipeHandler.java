package forestry.factory.recipes.jei.bottler;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.recipes.BottlerRecipe;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BottlerRecipeHandler implements IRecipeHandler<BottlerRecipeWrapper> {

	@Nonnull
	@Override
	public Class<BottlerRecipeWrapper> getRecipeClass() {
		return BottlerRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull BottlerRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull BottlerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull BottlerRecipeWrapper wrapper) {
		BottlerRecipe recipe = wrapper.getRecipe();
		if (recipe.input == null || recipe.input.amount <= 0) {
			return false;
		}
		if (recipe.empty == null) {
			return false;
		}
		return recipe.input != null;
	}

}
