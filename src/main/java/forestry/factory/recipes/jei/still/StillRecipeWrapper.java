package forestry.factory.recipes.jei.still;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import forestry.api.recipes.IStillRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

public class StillRecipeWrapper extends ForestryRecipeWrapper<IStillRecipe>{
	
	public StillRecipeWrapper(@Nonnull IStillRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(recipe.getInput());
	}
	
	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(recipe.getOutput());
	}

}
