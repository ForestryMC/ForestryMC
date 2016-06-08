package forestry.factory.recipes.jei.fermenter;

import javax.annotation.Nonnull;

import forestry.api.recipes.IFermenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FermenterRecipeHandler implements IRecipeHandler<FermenterRecipeWrapper> {
	
	@Nonnull
	@Override
	public Class<FermenterRecipeWrapper> getRecipeClass() {
		return FermenterRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.FERMENTER;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull FermenterRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.FERMENTER;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull FermenterRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull FermenterRecipeWrapper wrapper) {
		IFermenterRecipe recipe = wrapper.getRecipe();
		if (recipe.getFermentationValue() <= 0) {
			return false;
		}
		if (recipe.getModifier() <= 0) {
			return false;
		}
		if (recipe.getFluidResource() == null) {
			return false;
		}
		if (recipe.getResource() == null) {
			return false;
		}
		return recipe.getOutput() != null;
	}

}
