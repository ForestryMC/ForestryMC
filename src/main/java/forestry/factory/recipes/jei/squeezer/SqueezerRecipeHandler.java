package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SqueezerRecipeHandler<R extends ForestryRecipeWrapper> implements IRecipeHandler<R> {
	
	@Nonnull
	private Class<R> clazz;
	@Nonnull
	private String UID;
	
	public SqueezerRecipeHandler(@Nonnull Class<R> clazz, @Nonnull String UID) {
		this.clazz = clazz;
		this.UID = UID;
	}
	
	@Nonnull
	@Override
	public Class<R> getRecipeClass() {
		return clazz;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return UID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(R recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(R wrapper) {
		if(wrapper.getFluidOutputs() == null || wrapper.getFluidOutputs().isEmpty()){
			return false;
		}
		if(wrapper.getInputs() == null || wrapper.getInputs().isEmpty()){
			return false;
		}
		if(wrapper.getOutputs() == null || wrapper.getOutputs().isEmpty()){
			return false;
		}
		if(wrapper.getRecipe() instanceof ISqueezerContainerRecipe){
			ISqueezerContainerRecipe recipe = (ISqueezerContainerRecipe) wrapper.getRecipe();
			return recipe.getProcessingTime() > 0 && recipe.getRemnantsChance() > 0;
		}else{
			ISqueezerRecipe recipe = (ISqueezerRecipe) wrapper.getRecipe();
			return recipe.getProcessingTime() > 0 && recipe.getRemnantsChance() > 0;
		}
	}

}
