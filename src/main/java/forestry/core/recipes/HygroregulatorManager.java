package forestry.core.recipes;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import deleteme.RegistryNameFinder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;

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
	public Optional<IHygroregulatorRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack liquid) {
		if (liquid.isEmpty()) {
			return Optional.empty();
		}

		return getRecipes(recipeManager)
				.filter(recipe -> {
					FluidStack resource = recipe.getResource();
					return resource.isFluidEqual(liquid);
				})
				.findFirst();
	}

	@Override
	public Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager)
				.map(recipe -> RegistryNameFinder.getRegistryName(recipe.getResource().getFluid()))
				.collect(Collectors.toSet());
	}
}
