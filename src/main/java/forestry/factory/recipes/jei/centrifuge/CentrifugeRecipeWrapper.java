package forestry.factory.recipes.jei.centrifuge;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class CentrifugeRecipeWrapper extends ForestryRecipeWrapper<ICentrifugeRecipe>{
	
	public CentrifugeRecipeWrapper(@Nonnull ICentrifugeRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List getInputs() {
		return Collections.singletonList(recipe.getInput());
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.getAllProducts().keySet());
	}

}
