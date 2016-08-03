package forestry.factory.recipes.jei.fermenter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

public class FermenterRecipeWrapper extends ForestryRecipeWrapper<IFermenterRecipe> {
	
	@Nonnull
	private final ItemStack fermentable;
	
	public FermenterRecipeWrapper(@Nonnull IFermenterRecipe recipe, @Nonnull ItemStack fermentable) {
		super(recipe);
		this.fermentable = fermentable;
	}

	@Nonnull
	@Override
	public List getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		for (FermenterFuel fuel : FuelManager.fermenterFuel.values()) {
			inputs.add(fuel.getItem());
		}
		inputs.add(fermentable);
		return inputs;
	}

	@Nonnull
	public ItemStack getFermentable() {
		return fermentable;
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidInputs() {
		FluidStack input = getRecipe().getFluidResource().copy();
		input.amount = getRecipe().getFermentationValue();
		return Collections.singletonList(input);
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidOutputs() {
		int amount = Math.round(getRecipe().getFermentationValue() * getRecipe().getModifier());
		if (fermentable.getItem() instanceof IVariableFermentable) {
			amount *= ((IVariableFermentable) fermentable.getItem()).getFermentationModifier(fermentable);
		}
		FluidStack output = new FluidStack(getRecipe().getOutput(), amount);
		return Collections.singletonList(output);
	}

}
