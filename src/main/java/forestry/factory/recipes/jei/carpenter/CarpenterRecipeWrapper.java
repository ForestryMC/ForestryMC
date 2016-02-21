package forestry.factory.recipes.jei.carpenter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe>{
	
	public CarpenterRecipeWrapper(@Nonnull ICarpenterRecipe recipe) {
		super(recipe);
	}

	@Nonnull
	@Override
	public List getInputs() {
		List<Object> inputs = new ArrayList<>();
		Collections.addAll(inputs, getRecipe().getCraftingGridRecipe().getIngredients());
		inputs.add(getRecipe().getBox());
		return inputs;
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(getRecipe().getFluidResource());
	}

	@Nonnull
	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(getRecipe().getCraftingGridRecipe().getRecipeOutput());
	}

}
