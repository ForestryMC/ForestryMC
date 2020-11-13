package forestry.core.recipes;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import net.minecraft.fluid.Fluid;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IHygroregulatorManager;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.factory.recipes.AbstractCraftingProvider;

public class HygroregulatorManager extends AbstractCraftingProvider<IHygroregulatorRecipe> implements IHygroregulatorManager {

	public HygroregulatorManager() {
		super(IHygroregulatorRecipe.TYPE);
	}

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

	public Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (IHygroregulatorRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getResource();
				recipeFluids.add(fluidStack.getFluid());
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}
}
