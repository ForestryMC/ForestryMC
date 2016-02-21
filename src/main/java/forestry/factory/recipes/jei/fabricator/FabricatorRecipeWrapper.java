package forestry.factory.recipes.jei.fabricator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class FabricatorRecipeWrapper extends ForestryRecipeWrapper<IFabricatorRecipe>{
	
	public FabricatorRecipeWrapper(@Nonnull IFabricatorRecipe recipe) {
		super(recipe);
	}
	
	@Override
	public List getInputs() {
		List<Object> inputs = new ArrayList<>();
		Collections.addAll(inputs, getRecipe().getIngredients());
		inputs.add(getRecipe().getPlan());
		return inputs;
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.singletonList(getRecipe().getLiquid());
	}

	@Nonnull
	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(getRecipe().getRecipeOutput());
	}

}
