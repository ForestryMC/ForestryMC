package forestry.factory.recipes.jei.still;

import javax.annotation.Nonnull;
import java.util.Collections;

import forestry.api.recipes.IStillRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.fluids.FluidStack;

public class StillRecipeWrapper extends ForestryRecipeWrapper<IStillRecipe> {
	
	public StillRecipeWrapper(@Nonnull IStillRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputs(FluidStack.class, Collections.singletonList(getRecipe().getInput()));
		ingredients.setOutput(FluidStack.class, getRecipe().getOutput());
	}
}
