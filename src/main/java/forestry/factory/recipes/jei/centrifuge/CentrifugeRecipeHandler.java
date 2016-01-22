package forestry.factory.recipes.jei.centrifuge;

import javax.annotation.Nonnull;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

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

	@Override
	public IRecipeWrapper getRecipeWrapper(CentrifugeRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(CentrifugeRecipeWrapper wrapper){
		ICentrifugeRecipe recipe = wrapper.getRecipe();
		if(recipe.getProcessingTime() <= 0){
			return false;
		}
		if(recipe.getInput() == null){
			return false;
		}
		int inputCount = 0;
		for(ItemStack ingredient : recipe.getAllProducts().keySet()){
			inputCount++;
		}
		return inputCount > 0;
	}

}
