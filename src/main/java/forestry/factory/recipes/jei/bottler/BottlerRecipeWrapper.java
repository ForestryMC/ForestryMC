package forestry.factory.recipes.jei.bottler;

import java.util.Collections;
import java.util.List;

import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.BottlerRecipe;
import net.minecraftforge.fluids.FluidStack;

public class BottlerRecipeWrapper extends ForestryRecipeWrapper<BottlerRecipe> {

	public BottlerRecipeWrapper(BottlerRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(recipe.input);
	}
	
	@Override
	public List getInputs() {
		return Collections.singletonList(recipe.empty);
	}
	
	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.filled);
	}

}
