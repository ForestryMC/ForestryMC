package forestry.factory.recipes.jei.rainmaker;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RainmakerRecipeHandler implements IRecipeHandler<RainmakerRecipeWrapper> {
	@Nonnull
	@Override
	public Class<RainmakerRecipeWrapper> getRecipeClass() {
		return RainmakerRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.RAINMAKER;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull RainmakerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull RainmakerRecipeWrapper wrapper) {
		return !wrapper.getInputs().isEmpty();
	}
}
