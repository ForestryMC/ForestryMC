package forestry.factory.recipes.jei.squeezer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SqueezerRecipeWrapper extends ForestryRecipeWrapper<ISqueezerRecipe>{
	
	public SqueezerRecipeWrapper(@Nonnull ISqueezerRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		for(ItemStack ingredient : recipe.getResources()){
			inputs.add(ingredient);
		}
		return inputs;
	}
	
	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(recipe.getFluidOutput());
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.getRemnants());
	}

}
