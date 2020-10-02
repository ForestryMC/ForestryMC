package forestry.core.recipes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IHygroregulatorManager;
import forestry.api.recipes.IHygroregulatorRecipe;

public class HygroregulatorManager implements IHygroregulatorManager {

	@Override
	public void addRecipe(FluidStack resource, int transferTime, float tempChange, float humidChange) {
		addRecipe(new HygroregulatorRecipe(IForestryRecipe.anonymous(), resource, transferTime, humidChange, tempChange));
	}

	@Nullable
	public IHygroregulatorRecipe findMatchingRecipe(FluidStack liquid) {
		if (liquid.getAmount() <= 0) {
			return null;
		}
		for (IHygroregulatorRecipe recipe : recipes) {
			FluidStack resource = recipe.getResource();
			if (resource.isFluidEqual(liquid)) {
				return recipe;
			}
		}
		return null;
	}

	@Override
	public boolean addRecipe(IHygroregulatorRecipe recipe) {
		return recipes.add(recipe);
	}

	public Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (IHygroregulatorRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getResource();
				recipeFluids.add(fluidStack.getFluid());
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}

	@Override
	public Collection<IHygroregulatorRecipe> getRecipes(RecipeManager manager) {
		return ICraftingProvider.findRecipes(manager, IHygroregulatorRecipe.TYPE);
	}
}
