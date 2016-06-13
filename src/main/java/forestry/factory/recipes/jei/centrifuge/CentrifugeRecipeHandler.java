package forestry.factory.recipes.jei.centrifuge;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CentrifugeRecipeHandler implements IRecipeHandler<CentrifugeRecipeWrapper> {
	
	@Nonnull
	@Override
	public Class<CentrifugeRecipeWrapper> getRecipeClass() {
		return CentrifugeRecipeWrapper.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull CentrifugeRecipeWrapper recipe) {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CentrifugeRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CentrifugeRecipeWrapper wrapper) {
		ICentrifugeRecipe recipe = wrapper.getRecipe();
		if (recipe.getProcessingTime() <= 0) {
			return false;
		}
		if (recipe.getInput() == null) {
			return false;
		}
		int inputCount = 0;
		for (ItemStack ingredient : recipe.getAllProducts().keySet()) {
			inputCount++;
		}
		return inputCount > 0;
	}

}
