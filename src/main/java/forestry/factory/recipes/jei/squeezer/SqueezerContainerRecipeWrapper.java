package forestry.factory.recipes.jei.squeezer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.core.fluids.FluidHelper;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SqueezerContainerRecipeWrapper extends ForestryRecipeWrapper<ISqueezerContainerRecipe>{
	
	public SqueezerContainerRecipeWrapper(@Nonnull ISqueezerContainerRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List getInputs() {
		return FluidHelper.getAllFilledContainers(recipe.getEmptyContainer());
	}
	
	@Override
	public List<FluidStack> getFluidOutputs() {
		List<FluidStack> fluids = new ArrayList<>();
		for (ItemStack ingredient : FluidHelper.getAllFilledContainers(recipe.getEmptyContainer())) {
			FluidStack fluidStack = FluidHelper.getFluidStackInContainer(ingredient);
			fluids.add(fluidStack);
		}
		return fluids;
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.getRemnants());
	}

}
