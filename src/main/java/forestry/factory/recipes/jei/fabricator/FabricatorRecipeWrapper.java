package forestry.factory.recipes.jei.fabricator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class FabricatorRecipeWrapper extends ForestryRecipeWrapper<IFabricatorRecipe> {

	public FabricatorRecipeWrapper(IFabricatorRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IFabricatorRecipe recipe = getRecipe();

		NonNullList<NonNullList<ItemStack>> itemInputs = recipe.getIngredients();
		List<List<ItemStack>> inputStacks = new ArrayList<>();
		for (List<ItemStack> stacks : itemInputs) {
			List<ItemStack> copy = new ArrayList<>();
			copy.addAll(stacks);
			inputStacks.add(copy);
		}

		ingredients.setInputLists(ItemStack.class, inputStacks);

		ingredients.setInputs(FluidStack.class, Collections.singletonList(getRecipe().getLiquid()));

		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}
}
