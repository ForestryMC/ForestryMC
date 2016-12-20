package forestry.factory.recipes.jei.carpenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe> {

	public CarpenterRecipeWrapper(ICarpenterRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ICarpenterRecipe recipe = getRecipe();
		IDescriptiveRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();
		NonNullList<NonNullList<ItemStack>> inputs = craftingGridRecipe.getIngredients();

		List<List<ItemStack>> inputStacks = new ArrayList<>();
		for (List<ItemStack> stacks : inputs) {
			List<ItemStack> copy = new ArrayList<>();
			copy.addAll(stacks);
			inputStacks.add(copy);
		}

		ItemStack box = recipe.getBox();
		if (!box.isEmpty()) {
			inputStacks.add(Collections.singletonList(box));
		}
		ingredients.setInputLists(ItemStack.class, inputStacks);

		FluidStack fluidResource = recipe.getFluidResource();
		if (fluidResource != null) {
			ingredients.setInputs(FluidStack.class, Collections.singletonList(fluidResource));
		}

		ItemStack recipeOutput = craftingGridRecipe.getRecipeOutput();
		ingredients.setOutput(ItemStack.class, recipeOutput);
	}
}
