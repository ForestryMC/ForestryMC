package forestry.core.recipes;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.fluid.Fluid;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IHygroregulatorManager;
import forestry.api.recipes.IHygroregulatorRecipe;

public class HygroregulatorManager implements IHygroregulatorManager {
	private static final Set<IHygroregulatorRecipe> recipes = new HashSet<>();
	private static final Set<Fluid> recipeFluids = new HashSet<>();

	@Override
	public void addRecipe(FluidStack resource, int transferTime, float tempChange, float humidChange) {
		addRecipe(new HygroregulatorRecipe(resource, transferTime, humidChange, tempChange));
	}

	@Nullable
	public static IHygroregulatorRecipe findMatchingRecipe(FluidStack liquid) {
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

	@Override
	public boolean removeRecipe(IHygroregulatorRecipe recipe) {
		return recipes.remove(recipe);
	}

	public static Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (IHygroregulatorRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getResource();
				recipeFluids.add(fluidStack.getFluid());
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}

	@Override
	public Set<IHygroregulatorRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}
}
