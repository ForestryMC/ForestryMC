package forestry.factory.recipes.jei.carpenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe>{
	
	public CarpenterRecipeWrapper(@Nonnull ICarpenterRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List getInputs() {
		List<Object> inputs = new ArrayList<>();
		for(Object ingredient : recipe.getCraftingGridRecipe().getIngredients()){
			inputs.add(ingredient);
		}
		inputs.add(recipe.getBox());
		return inputs;
	}
	
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(recipe.getFluidResource());
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.getCraftingGridRecipe().getRecipeOutput());
	}

}
