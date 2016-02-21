package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ISqueezerRecipe;

public class SqueezerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerRecipe> {
	
	public SqueezerRecipeWrapper(@Nonnull ISqueezerRecipe recipe) {
		super(recipe);
	}
	
	@Nonnull
	@Override
	public List<ItemStack> getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		Collections.addAll(inputs, getRecipe().getResources());
		return inputs;
	}
	
	@Nonnull
	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(getRecipe().getFluidOutput());
	}

	@Nonnull
	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(getRecipe().getRemnants());
	}

	@Override
	public float getRemnantsChance() {
		return getRecipe().getRemnantsChance();
	}
}
