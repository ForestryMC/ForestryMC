package forestry.factory.recipes.jei.centrifuge;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class CentrifugeRecipeWrapper extends ForestryRecipeWrapper<ICentrifugeRecipe>{
	
	public CentrifugeRecipeWrapper(@Nonnull ICentrifugeRecipe recipe) {
		super(recipe);
	}

	@Nonnull
	@Override
	public List getInputs() {
		return Collections.singletonList(getRecipe().getInput());
	}

	@Nonnull
	@Override
	public List getOutputs() {
		return Collections.singletonList(getRecipe().getAllProducts().keySet());
	}

}
