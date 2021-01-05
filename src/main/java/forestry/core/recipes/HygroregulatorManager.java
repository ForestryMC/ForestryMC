package forestry.core.recipes;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

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

	@Override
	@Nullable
	public IHygroregulatorRecipe findMatchingRecipe(RecipeManager recipeManager, FluidStack liquid) {
		if (liquid.getAmount() <= 0) {
			return null;
		}

		for (IHygroregulatorRecipe recipe : getRecipes(recipeManager)) {
			FluidStack resource = recipe.getResource();
			if (resource.isFluidEqual(liquid)) {
				return recipe;
			}
		}
		return null;
	}

	@Override
	public Set<ResourceLocation> getRecipeFluids(RecipeManager recipeManager) {
		return getRecipes(recipeManager).stream()
				.map(recipe -> recipe.getResource().getFluid().getRegistryName())
				.collect(Collectors.toSet());
	}
}
