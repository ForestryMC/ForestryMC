package forestry.factory.recipes.jei.bottler;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.BottlerRecipe;

public class BottlerRecipeWrapper extends ForestryRecipeWrapper<BottlerRecipe> {

	public BottlerRecipeWrapper(BottlerRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(getRecipe().input);
	}
	
	@Override
	public List<ItemStack> getInputs() {
		return Collections.singletonList(getRecipe().empty);
	}
	
	@Override
	public List getOutputs() {
		return Collections.singletonList(getRecipe().filled);
	}

}
