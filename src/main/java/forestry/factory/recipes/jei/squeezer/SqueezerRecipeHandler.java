package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SqueezerRecipeHandler<R extends ForestryRecipeWrapper> implements IRecipeHandler<R> {
	
	@Nonnull
	private final Class<R> recipeClass;
	
	public SqueezerRecipeHandler(@Nonnull Class<R> recipeClass) {
		this.recipeClass = recipeClass;
	}
	
	@Nonnull
	@Override
	public Class<R> getRecipeClass() {
		return recipeClass;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.SQUEEZER;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull R recipe) {
		return ForestryRecipeCategoryUid.SQUEEZER;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull R recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull R wrapper) {
		if (wrapper.getFluidOutputs() == null || wrapper.getFluidOutputs().isEmpty()) {
			return false;
		}
		if (wrapper.getInputs() == null || wrapper.getInputs().isEmpty()) {
			return false;
		}
		return true;
	}

}
