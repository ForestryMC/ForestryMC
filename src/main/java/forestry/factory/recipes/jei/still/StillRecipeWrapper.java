package forestry.factory.recipes.jei.still;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class StillRecipeWrapper extends ForestryRecipeWrapper<IStillRecipe> {
	
	public StillRecipeWrapper(@Nonnull IStillRecipe recipe) {
		super(recipe);
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(getRecipe().getInput());
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(getRecipe().getOutput());
	}

}
