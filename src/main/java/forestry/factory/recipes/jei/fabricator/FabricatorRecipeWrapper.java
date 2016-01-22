package forestry.factory.recipes.jei.fabricator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

public class FabricatorRecipeWrapper extends ForestryRecipeWrapper<IFabricatorRecipe>{
	
	public FabricatorRecipeWrapper(@Nonnull IFabricatorRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List getInputs() {
		List<Object> inputs = new ArrayList<>();
		for(Object ingredient : recipe.getIngredients()){
			inputs.add(ingredient);
		}
		inputs.add(recipe.getPlan());
		return inputs;
	}
	
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(recipe.getLiquid());
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.getRecipeOutput());
	}

}
